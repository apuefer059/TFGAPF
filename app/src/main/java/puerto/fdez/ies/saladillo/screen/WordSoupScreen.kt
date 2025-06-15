package puerto.fdez.ies.saladillo.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import puerto.fdez.ies.saladillo.R
import puerto.fdez.ies.saladillo.model.WordSoupViewModel
import puerto.fdez.ies.saladillo.ui.theme.InkBlue
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSoupScreen(
    viewModel: WordSoupViewModel,
    onBack: () -> Unit
) {
    val grid = viewModel.grid
    val placedWords = viewModel.placedWords
    val foundWords = viewModel.foundWords
    val highlighted = viewModel.highlightedPath
    val targetWords = viewModel.targetWords

    val cellSize = 28.dp
    val cellPx = with(LocalDensity.current) { cellSize.toPx() }

    var selected by remember { mutableStateOf(listOf<Pair<Int, Int>>()) }
    var isDragging by remember { mutableStateOf(false) }

    NotebookScaffold(
        title = stringResource(R.string.word_soup_title),
        onBack = onBack,
        actions = {
            Button(
                onClick = {
                    selected = emptyList()
                    viewModel.newGame()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = InkBlue
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(stringResource(R.string.word_soup_new_game))
            }
        }
    ) { padding ->

        if (grid.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.loading_word_search), color = InkBlue)
            }
            return@NotebookScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Words: ${foundWords.size}/${targetWords.size}",
                style = MaterialTheme.typography.titleMedium,
                color = InkBlue
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .wrapContentWidth()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                val cell = offset.toCell(cellPx)
                                selected = if (cell != null) listOf(cell) else emptyList()
                            },
                            onDrag = { change, _ ->
                                val cell = change.position.toCell(cellPx)
                                if (cell != null && cell !in selected) {
                                    selected = selected + cell
                                }
                            },
                            onDragEnd = {
                                if (isLinearPath(selected)) {
                                    viewModel.checkSelectedPath(selected)
                                }
                                selected = emptyList()
                                isDragging = false
                            }
                        )
                    }
            ) {
                Column {
                    for (x in grid.indices) {
                        Row {
                            for (y in grid[x].indices) {
                                val position = x to y
                                val isSelected = selected.contains(position)
                                val isFound = placedWords.any {
                                    it.value.contains(position) && foundWords.contains(it.key)
                                }
                                val isHighlight = highlighted.contains(position)

                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .padding(1.dp)
                                        .border(
                                            width = 1.dp,
                                            color = InkBlue,
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                        .background(
                                            color = when {
                                                isHighlight -> Color.Yellow
                                                isSelected -> Color.Cyan
                                                isFound -> Color.Green.copy(alpha = 0.3f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = !isDragging) {
                                            selected = if (isSelected) {
                                                selected - position
                                            } else {
                                                selected + position
                                            }

                                            if (isLinearPath(selected)) {
                                                viewModel.checkSelectedPath(selected)
                                                selected = emptyList()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = grid[x][y].toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (foundWords.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    foundWords.forEach {
                        Text(
                            text = "${it.word}: ${it.definition}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlue
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            if (foundWords.size == targetWords.size && targetWords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.you_found_all_words),
                    style = MaterialTheme.typography.titleLarge,
                    color = InkBlue
                )
            }
        }
    }
}

// Helper functions
private fun Offset.toCell(cellSizePx: Float): Pair<Int, Int>? {
    val row = floor(y / cellSizePx).toInt()
    val col = floor(x / cellSizePx).toInt()
    return if (row in 0 until 15 && col in 0 until 15) row to col else null
}

private fun isLinearPath(path: List<Pair<Int, Int>>): Boolean {
    if (path.size < 2) return false
    val dx = path[1].first - path[0].first
    val dy = path[1].second - path[0].second
    return path.zipWithNext().all { (a, b) ->
        b.first - a.first == dx && b.second - a.second == dy
    }
}
