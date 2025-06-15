package puerto.fdez.ies.saladillo.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import puerto.fdez.ies.saladillo.R
import puerto.fdez.ies.saladillo.model.BattleShipViewModel
import puerto.fdez.ies.saladillo.model.Board
import puerto.fdez.ies.saladillo.model.CellState
import puerto.fdez.ies.saladillo.model.GamePhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleshipScreen(viewModel: BattleShipViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val enemyBoard by viewModel.enemyBoard.collectAsState()
    val selectedShip by viewModel.selectedShipIndex.collectAsState()

    NotebookScaffold(
        title = stringResource(id = R.string.battleship_title),
        onBack = onBack,
        actions = {
            if (uiState.currentPhase == GamePhase.GAME_OVER) {
                Button(
                    onClick = { viewModel.resetGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(id = R.string.reset))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(uiState.gameMessage, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.currentPhase == GamePhase.BATTLE || uiState.currentPhase == GamePhase.GAME_OVER) {
                Text(stringResource(id = R.string.enemy_board))
                Spacer(modifier = Modifier.height(8.dp))

                GameBoard(
                    board = enemyBoard,
                    showShips = false,
                    revealShips = uiState.currentPhase == GamePhase.GAME_OVER,
                    onCellClick = { row, col -> viewModel.onEnemyCellClick(row, col) },
                    onCellDoubleClick = { _, _ -> }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(stringResource(id = R.string.your_board))
                Spacer(modifier = Modifier.height(8.dp))

                GameBoard(
                    board = uiState.playerBoard,
                    showShips = true,
                    onCellClick = { _, _ -> },
                    onCellDoubleClick = { _, _ -> }
                )

            } else {
                val totalShips = 6
                val placed = totalShips - viewModel.getAvailableShips().size

                Text(stringResource(id = R.string.place_ships_count, placed, totalShips))
                Spacer(modifier = Modifier.height(16.dp))

                GameBoard(
                    board = uiState.playerBoard,
                    showShips = true,
                    onCellClick = { row, col ->
                        if (selectedShip >= 0) {
                            viewModel.placeSelectedShip(row, col)
                        } else {
                            viewModel.onCellClick(row, col)
                        }
                    },
                    onCellDoubleClick = { row, col -> viewModel.onCellDoubleClick(row, col) }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(id = R.string.available_ships))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    viewModel.getAvailableShips().forEachIndexed { index, ship ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clickable { viewModel.selectShip(index) }
                                .padding(4.dp)
                        ) {
                            repeat(ship.size) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (selectedShip == index) Color.Blue else Color.DarkGray,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.startGame() },
                    enabled = viewModel.getAvailableShips().isEmpty(),
                    shape = CircleShape
                ) {
                    Text(stringResource(id = R.string.start))
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameBoard(
    board: Board,
    showShips: Boolean,
    revealShips: Boolean = false,
    onCellClick: (Int, Int) -> Unit,
    onCellDoubleClick: (Int, Int) -> Unit
) {
    Column {
        board.cells.forEach { row ->
            Row {
                row.forEach { cell ->
                    val color = when (cell.state) {
                        CellState.SUNK -> Color.Red
                        CellState.HIT -> Color.Yellow
                        CellState.MISS -> Color.Blue
                        CellState.SHIP -> if (showShips || revealShips) Color.Green else Color.White
                        CellState.EMPTY -> Color.White
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, Color.Black)
                            .background(color)
                            .combinedClickable(
                                onClick = { onCellClick(cell.row, cell.col) },
                                onDoubleClick = { onCellDoubleClick(cell.row, cell.col) }
                            )
                    )
                }
            }
        }
    }
}
