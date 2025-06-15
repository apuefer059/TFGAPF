package puerto.fdez.ies.saladillo.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import puerto.fdez.ies.saladillo.R
import puerto.fdez.ies.saladillo.model.Cell
import puerto.fdez.ies.saladillo.model.TicTacToeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isVsAI = viewModel.isVsAI
    val isThinking = viewModel.isThinking
    val winningCells = viewModel.winningCells

    NotebookScaffold(
        title = stringResource(R.string.tic_tac_toe_title),
        onBack = onBack,
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.vs_ai),
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = isVsAI,
                    onCheckedChange = { viewModel.updateVsAI(it) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = when {
                        state.winner != null -> stringResource(R.string.winner, state.winner!!.name)
                        state.isDraw -> stringResource(R.string.draw)
                        else -> stringResource(R.string.turn, state.currentPlayer.name)
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isThinking) {
                    Text(
                        text = stringResource(R.string.ai_thinking),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    for (row in state.board) {
                        Row {
                            for (cell in row) {
                                TicTacToeCell(
                                    cell = cell,
                                    isWinningCell = winningCells.contains(cell.row to cell.col)
                                ) {
                                    viewModel.onCellClicked(cell.row, cell.col)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.resetGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.reset), )
                }
            }
        }
    }
}


@Composable
fun TicTacToeCell(
    cell: Cell,
    isWinningCell: Boolean,
    onClick: () -> Unit
) {
    val targetColor = when {
        isWinningCell -> Color.Green.copy(alpha = 0.5f)
        cell.player != null -> Color.LightGray.copy(alpha = 0.8f)
        else -> Color.White.copy(alpha = 0.6f)
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        label = "CellColorAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = cell.player == null) { onClick() }
    ) {
        Text(
            text = cell.player?.name ?: "",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


