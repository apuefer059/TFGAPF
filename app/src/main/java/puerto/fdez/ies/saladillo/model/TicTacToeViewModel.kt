package puerto.fdez.ies.saladillo.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Player {
    X,
    O,
}

data class Cell(
    val row: Int,
    val col: Int,
    val player: Player? = null
)

data class TicTacToeState(
    val board: List<List<Cell>> = List(3) { row ->
        List(3) { col ->
            Cell(row, col, null)
        }
    },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false
)

class TicTacToeViewModel (initialIsVsAI: Boolean = false) : ViewModel() {

    private val _uiState = MutableStateFlow(TicTacToeState())
    val uiState: StateFlow<TicTacToeState> = _uiState

    var isThinking by mutableStateOf(false)
        private set

    var isVsAI by mutableStateOf(initialIsVsAI)
        private set

    val aiThinkingDelayRange: LongRange = 2000L..3000L

    val isGameOver: Boolean
        get() = uiState.value.winner != null || uiState.value.isDraw

    fun updateVsAI(enabled: Boolean) {
        isVsAI = enabled
        resetGame()
    }

    fun onCellClicked(row: Int, col: Int) {
        val state = _uiState.value
        val cell = state.board[row][col]

        if (cell.player != null || state.winner != null) return

        val updatedBoard = cloneBoard(state.board)
        updatedBoard[row][col] = cell.copy(player = state.currentPlayer)

        val winner = checkWinner(updatedBoard)
        val isDraw = winner == null && updatedBoard.flatten().all { it.player != null }

        _uiState.update {
            it.copy(
                board = updatedBoard,
                currentPlayer = if (winner == null && !isDraw) togglePlayer(it.currentPlayer) else it.currentPlayer,
                winner = winner,
                isDraw = isDraw
            )
        }

        // IA juega si está activada y no terminó el juego
        if (isVsAI && !isDraw && winner == null && _uiState.value.currentPlayer == Player.O) {
            makeAIMove()
        }
    }

    private fun makeAIMove() {
        viewModelScope.launch {
            isThinking = true
            delay(aiThinkingDelayRange.random())
            isThinking = false

            val board = _uiState.value.board
            val emptyCells = board.flatten().filter { it.player == null }

            val useBestMove = (0..100).random() < 75
            val move = if (useBestMove) {
                findBestMove(board) ?: emptyCells.random()
            } else {
                emptyCells.random()
            }

            move?.let {
                onCellClicked(it.row, it.col)
            }
        }
    }

    private fun findBestMove(board: List<List<Cell>>): Cell? {
        var bestScore = Int.MIN_VALUE
        var bestMove: Cell? = null

        for (row in board) {
            for (cell in row) {
                if (cell.player == null) {
                    val newBoard = cloneBoard(board)
                    newBoard[cell.row][cell.col] = cell.copy(player = Player.O)
                    val score = minimax(newBoard, depth = 0, isMaximizing = false)
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = cell
                    }
                }
            }
        }

        return bestMove
    }

    private fun minimax(board: List<List<Cell>>, depth: Int, isMaximizing: Boolean): Int {
        val winner = checkWinner(board)
        if (winner == Player.X) return -10 + depth
        if (winner == Player.O) return 10 - depth
        if (board.flatten().all { it.player != null }) return 0

        if (isMaximizing) {
            var bestScore = Int.MIN_VALUE
            for (row in board) {
                for (cell in row) {
                    if (cell.player == null) {
                        val newBoard = cloneBoard(board)
                        newBoard[cell.row][cell.col] = cell.copy(player = Player.O)
                        val score = minimax(newBoard, depth + 1, isMaximizing = false)
                        bestScore = maxOf(score, bestScore)
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (row in board) {
                for (cell in row) {
                    if (cell.player == null) {
                        val newBoard = cloneBoard(board)
                        newBoard[cell.row][cell.col] = cell.copy(player = Player.X)
                        val score = minimax(newBoard, depth + 1, isMaximizing = true)
                        bestScore = minOf(score, bestScore)
                    }
                }
            }
            return bestScore
        }
    }

    fun resetGame() {
        _uiState.value = TicTacToeState()
    }

    private fun togglePlayer(current: Player): Player {
        return if (current == Player.X) Player.O else Player.X
    }

    private fun checkWinner(board: List<List<Cell>>): Player? {
        // Check rows
        board.forEach { row ->
            row.first().player?.let { player ->
                if (row.all { it.player == player }) return player
            }
        }

        // Check columns
        for (col in 0..2) {
            board[0][col].player?.let { player ->
                if ((0..2).all { row -> board[row][col].player == player }) return player
            }
        }

        // Check diagonals
        board[0][0].player?.let { player ->
            if ((0..2).all { i -> board[i][i].player == player }) return player
        }
        board[0][2].player?.let { player ->
            if ((0..2).all { i -> board[i][2 - i].player == player }) return player
        }

        return null
    }

    val winningCells: Set<Pair<Int, Int>>
        get() {
            // Check rows
            uiState.value.board.forEach { row ->
                row.first().player?.let { player ->
                    if (row.all { it.player == player }) {
                        return row.map { it.row to it.col }.toSet()
                    }
                }
            }

            // Check columns
            for (col in 0..2) {
                uiState.value.board[0][col].player?.let { player ->
                    if ((0..2).all { row -> uiState.value.board[row][col].player == player }) {
                        return (0..2).map { row -> row to col }.toSet()
                    }
                }
            }

            // Check diagonals
            uiState.value.board[0][0].player?.let { player ->
                if ((0..2).all { i -> uiState.value.board[i][i].player == player }) {
                    return (0..2).map { i -> i to i }.toSet()
                }
            }

            uiState.value.board[0][2].player?.let { player ->
                if ((0..2).all { i -> uiState.value.board[i][2 - i].player == player }) {
                    return (0..2).map { i -> i to (2 - i) }.toSet()
                }
            }

            return emptySet()
        }


    private fun cloneBoard(board: List<List<Cell>>): MutableList<MutableList<Cell>> {
        return board.map { row -> row.map { it.copy() }.toMutableList() }.toMutableList()
    }
}
