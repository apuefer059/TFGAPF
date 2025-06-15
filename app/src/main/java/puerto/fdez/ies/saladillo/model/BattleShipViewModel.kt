package puerto.fdez.ies.saladillo.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class CellState { EMPTY, SHIP, HIT, MISS, SUNK }
enum class GamePhase { PLACEMENT, BATTLE, GAME_OVER }

data class BattleShipCell(val row: Int, val col: Int, val state: CellState = CellState.EMPTY)
data class Board(val cells: List<List<BattleShipCell>>)
data class BattleshipUiState(
    val playerBoard: Board,
    val currentPhase: GamePhase = GamePhase.PLACEMENT,
    val gameMessage: String = "Place your ships"
)

data class Ship(val size: Int, var orientation: Boolean = true)
data class PlacedShip(val size: Int, var orientation: Boolean, var start: Pair<Int, Int>) {
    fun positions(): List<Pair<Int, Int>> {
        return (0 until size).map {
            if (orientation) start.first to (start.second + it)
            else (start.first + it) to start.second
        }
    }
}

class BattleShipViewModel : ViewModel() {

    private val gridSize = 10
    private val placedShips = mutableListOf<PlacedShip>()
    private val availableShips = mutableListOf(
        Ship(2), Ship(2), Ship(2),
        Ship(3), Ship(3),
        Ship(4)
    )
    private val enemyShips = mutableListOf<PlacedShip>()
    private val playerHits = mutableSetOf<Pair<Int, Int>>()
    private val enemyHits = mutableSetOf<Pair<Int, Int>>()

    private var aiTargetQueue = ArrayDeque<Pair<Int, Int>>() // IA objetivo
    private var aiLastHit: Pair<Int, Int>? = null            // Último acierto

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<BattleshipUiState> = _uiState

    private val _selectedShipIndex = MutableStateFlow(-1)
    val selectedShipIndex: StateFlow<Int> = _selectedShipIndex

    private val _enemyBoard = MutableStateFlow(createEmptyBoard())
    val enemyBoard: StateFlow<Board> = _enemyBoard

    var playerTurn = true
        private set

    private fun createInitialState(): BattleshipUiState {
        return BattleshipUiState(playerBoard = createEmptyBoard())
    }

    private fun createEmptyBoard(): Board {
        return Board(List(gridSize) { row ->
            List(gridSize) { col -> BattleShipCell(row, col) }
        })
    }

    fun getAvailableShips(): List<Ship> = availableShips

    fun selectShip(index: Int) {
        _selectedShipIndex.value = index
    }

    fun placeSelectedShip(row: Int, col: Int) {
        val selectedIndex = _selectedShipIndex.value
        if (selectedIndex !in availableShips.indices) return

        val ship = availableShips[selectedIndex]
        val primary = getShipPositions(row, col, ship.size, ship.orientation)

        if (primary.all { isValidPosition(it.first, it.second) && !isOccupied(it) }) {
            placedShips.add(PlacedShip(ship.size, ship.orientation, row to col))
            availableShips.removeAt(selectedIndex)
            _selectedShipIndex.value = -1
            updateBoard()
            return
        }

        val inverseStart = if (ship.orientation) {
            row to (col - (ship.size - 1))
        } else {
            (row - (ship.size - 1)) to col
        }

        val inverse = getShipPositions(inverseStart.first, inverseStart.second, ship.size, ship.orientation)
        if (inverse.all { isValidPosition(it.first, it.second) && !isOccupied(it) }) {
            placedShips.add(PlacedShip(ship.size, ship.orientation, inverseStart))
            availableShips.removeAt(selectedIndex)
            _selectedShipIndex.value = -1
            updateBoard()
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val ship = placedShips.firstOrNull { it.positions().contains(row to col) } ?: return
        val original = ship.orientation
        ship.orientation = !ship.orientation

        val newPos = ship.positions()
        if (newPos.all { isValidPosition(it.first, it.second) && !isOccupiedByOthers(it, ship) }) {
            updateBoard()
            return
        }

        val inverseStart = if (ship.orientation) {
            ship.start.first to (ship.start.second - (ship.size - 1))
        } else {
            (ship.start.first - (ship.size - 1)) to ship.start.second
        }

        val inverse = getShipPositions(inverseStart.first, inverseStart.second, ship.size, ship.orientation)
        if (inverse.all { isValidPosition(it.first, it.second) && !isOccupiedByOthers(it, ship) }) {
            ship.start = inverseStart
            updateBoard()
        } else {
            ship.orientation = original
        }
    }

    fun onCellDoubleClick(row: Int, col: Int) {
        val ship = placedShips.firstOrNull { it.positions().contains(row to col) } ?: return
        placedShips.remove(ship)
        availableShips.add(Ship(ship.size))
        updateBoard()
    }

    fun startGame() {
        setupEnemyShips()
        _uiState.value = _uiState.value.copy(
            currentPhase = GamePhase.BATTLE,
            gameMessage = "Battle started! Player begins."
        )
    }

    private fun setupEnemyShips() {
        enemyShips.clear()

        val shipSizes = listOf(4, 3, 3, 2, 2, 2)
        for (size in shipSizes) {
            val validPlacements = mutableListOf<PlacedShip>()

            for (row in 0 until gridSize) {
                for (col in 0 until gridSize) {
                    // Horizontal
                    val horizontal = PlacedShip(size, true, row to col)
                    if (horizontal.positions().all {
                            isValidPosition(it.first, it.second) &&
                                    !enemyShips.any { ship -> ship.positions().contains(it) }
                        }) {
                        validPlacements.add(horizontal)
                    }

                    // Vertical
                    val vertical = PlacedShip(size, false, row to col)
                    if (vertical.positions().all {
                            isValidPosition(it.first, it.second) &&
                                    !enemyShips.any { ship -> ship.positions().contains(it) }
                        }) {
                        validPlacements.add(vertical)
                    }
                }
            }

            if (validPlacements.isEmpty()) {
                throw IllegalStateException("Cant fit the boat in this space $size")
            }

            val chosen = validPlacements.random()
            enemyShips.add(chosen)
        }

        _enemyBoard.value = createEmptyBoard()
    }


    fun onEnemyCellClick(row: Int, col: Int) {
        if (!playerTurn || _uiState.value.currentPhase != GamePhase.BATTLE) return
        val hit = enemyShips.firstOrNull { it.positions().contains(row to col) }
        val newBoard = _enemyBoard.value.cells.map { it.toMutableList() }

        if (hit != null) {
            playerHits.add(row to col)
            newBoard[row][col] = newBoard[row][col].copy(state = CellState.HIT)
            if (isSunk(hit, playerHits)) markSunkShip(hit, newBoard)
            if (enemyShips.all { isSunk(it, playerHits) }) {
                _enemyBoard.value = Board(newBoard)
                _uiState.value = _uiState.value.copy(currentPhase = GamePhase.GAME_OVER, gameMessage = "You sunk all the enemy ships!")
                return
            }
        } else {
            newBoard[row][col] = newBoard[row][col].copy(state = CellState.MISS)
        }

        _enemyBoard.value = Board(newBoard)
        playerTurn = false
        simulateEnemyTurn()
    }

    private fun simulateEnemyTurn() {
        //Un poco de tiempo para simular que la IA piensa
        viewModelScope.launch {
            delay(1000)
        }
        val board = _uiState.value.playerBoard.cells
        val mutableBoard = board.map { it.toMutableList() }

        val target = getNextAITarget() ?: return

        enemyHits.add(target)
        val hitShip = placedShips.firstOrNull { it.positions().contains(target) }

        if (hitShip != null) {
            mutableBoard[target.first][target.second] =
                mutableBoard[target.first][target.second].copy(state = CellState.HIT)

            aiLastHit = target
            enqueueAdjacentTargets(target)

            if (isSunk(hitShip, enemyHits)) {
                markSunkShip(hitShip, mutableBoard)
                aiLastHit = null
                aiTargetQueue.clear()
            }

            if (placedShips.all { isSunk(it, enemyHits) }) {
                _uiState.value = _uiState.value.copy(
                    currentPhase = GamePhase.GAME_OVER,
                    gameMessage = "AI has sunk all your ships!"
                )
                return
            }
        } else {
            mutableBoard[target.first][target.second] =
                mutableBoard[target.first][target.second].copy(state = CellState.MISS)
        }

        _uiState.value = _uiState.value.copy(playerBoard = Board(mutableBoard))
        playerTurn = true
    }

    private fun getNextAITarget(): Pair<Int, Int>? {
        while (aiTargetQueue.isNotEmpty()) {
            val next = aiTargetQueue.removeFirst()
            if (next !in enemyHits && isValidPosition(next.first, next.second)) return next
        }

        val unknownCells = _uiState.value.playerBoard.cells.flatten()
            .map { it.row to it.col }
            .filter { it !in enemyHits }

        return unknownCells.randomOrNull()
    }

    private fun enqueueAdjacentTargets(hit: Pair<Int, Int>) {
        val (row, col) = hit
        val adjacent = listOf(
            row - 1 to col,
            row + 1 to col,
            row to col - 1,
            row to col + 1
        ).filter { isValidPosition(it.first, it.second) && it !in enemyHits }

        aiTargetQueue.addAll(adjacent)
    }

    private fun isSunk(ship: PlacedShip, hits: Set<Pair<Int, Int>>): Boolean {
        return ship.positions().all { hits.contains(it) }
    }

    private fun markSunkShip(ship: PlacedShip, board: List<MutableList<BattleShipCell>>) {
        ship.positions().forEach { (r, c) ->
            board[r][c] = board[r][c].copy(state = CellState.SUNK)
        }
    }

    private fun updateBoard() {
        val newCells = List(gridSize) { row ->
            List(gridSize) { col ->
                val state = if (placedShips.any { it.positions().contains(row to col) }) CellState.SHIP else CellState.EMPTY
                BattleShipCell(row, col, state)
            }
        }
        _uiState.value = _uiState.value.copy(playerBoard = Board(newCells))
    }

    private fun getShipPositions(row: Int, col: Int, size: Int, horizontal: Boolean): List<Pair<Int, Int>> {
        return (0 until size).map {
            if (horizontal) row to (col + it) else (row + it) to col
        }
    }

    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until gridSize && col in 0 until gridSize
    }

    private fun isOccupied(pos: Pair<Int, Int>): Boolean {
        return placedShips.any { it.positions().contains(pos) }
    }

    private fun isOccupiedByOthers(pos: Pair<Int, Int>, ship: PlacedShip): Boolean {
        return placedShips.any { it != ship && it.positions().contains(pos) }
    }

    fun resetGame() {
        placedShips.clear()
        availableShips.clear()
        availableShips.addAll(
            listOf(
                Ship(2), Ship(2), Ship(2),
                Ship(3), Ship(3),
                Ship(4)
            )
        )
        enemyShips.clear()
        playerHits.clear()
        enemyHits.clear()
        aiTargetQueue.clear()
        aiLastHit = null
        playerTurn = true

        _selectedShipIndex.value = -1
        _enemyBoard.value = createEmptyBoard()
        _uiState.value = createInitialState()
    }

}
