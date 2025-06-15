package puerto.fdez.ies.saladillo.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import puerto.fdez.ies.saladillo.R
import puerto.fdez.ies.saladillo.model.HangmanViewModel
import puerto.fdez.ies.saladillo.ui.theme.InkBlue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HangmanScreen(
    viewModel: HangmanViewModel,
    onBack: () -> Unit
) {
    val displayWord by viewModel.displayWord.collectAsState()
    val guessedLetters by viewModel.guessedLetters.collectAsState()
    val mistakes by viewModel.mistakes.collectAsState()
    val gameOver by viewModel.gameOver.collectAsState()
    val isWon by viewModel.isWon.collectAsState()
    val hint by viewModel.hint.collectAsState()
    val secretWord by viewModel.secretWord.collectAsState()
    val definition by viewModel.definition.collectAsState()

    NotebookScaffold(
        title = stringResource(R.string.hangman_title),
        onBack = onBack,
        actions = {
            if (gameOver) {
                Button(
                    onClick = { viewModel.startNewGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.new_game))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
        ) {
            HangmanAsciiArt(mistakes = mistakes)

            DisplayWord(word = displayWord)

            Text(
                text = stringResource(R.string.errors_label, mistakes, 6),
                style = MaterialTheme.typography.bodyLarge
            )

            if (mistakes >= 4 && !gameOver) {
                Text(
                    text = stringResource(R.string.hint_label, hint),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue
                )
            }

            if (gameOver) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isWon)
                            stringResource(R.string.you_win)
                        else
                            stringResource(R.string.you_lose_word, secretWord),
                        color = if (isWon) Color.Green else Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (!isWon && definition.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.definition_label, definition),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LetterGrid(
                    guessedLetters = guessedLetters,
                    isGameOver = gameOver
                ) { letter ->
                    viewModel.guessLetter(letter)
                }
            }
        }
    }
}





@Composable
fun HangmanAsciiArt(mistakes: Int) {
    val hangmanStages = listOf(
        """
          +---+
          |   |
              |
              |
              |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
              |
              |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
          |   |
              |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
         /|   |
              |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
         /|\  |
              |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
         /|\  |
         /    |
              |
        =========
        """.trimIndent(),
        """
          +---+
          |   |
          O   |
         /|\  |
         / \  |
              |
        =========
        """.trimIndent()
    )

    val clamped = mistakes.coerceIn(0, hangmanStages.lastIndex)

    Text(
        text = hangmanStages[clamped],
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 28.sp
        ),
        color = InkBlue)

}

@Composable
fun DisplayWord(word: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        word.forEach { char ->
            Text(
                text = char.toString(),
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Composable
fun LetterGrid(
    guessedLetters: Set<Char>,
    isGameOver: Boolean,
    onLetterClick: (Char) -> Unit
) {
    val letters = ('A'..'Z').toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(letters) { letter ->
            val isGuessed = guessedLetters.contains(letter)

            Button(
                onClick = { onLetterClick(letter) },
                enabled = !isGuessed && !isGameOver,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(letter.toString())
            }
        }
    }
}
