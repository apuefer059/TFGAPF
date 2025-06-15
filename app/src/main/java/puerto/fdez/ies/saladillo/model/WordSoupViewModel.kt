package puerto.fdez.ies.saladillo.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import puerto.fdez.ies.saladillo.data.database.entities.WordEntity
import puerto.fdez.ies.saladillo.data.repositories.WordRepository
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WordSoupViewModel @Inject constructor(private val wordRepo: WordRepository) : ViewModel() {

    companion object {
        const val GRID_SIZE = 13
    }

    var grid by mutableStateOf(List(GRID_SIZE) { List(GRID_SIZE) { ' ' } })
        private set

    var foundWords by mutableStateOf(setOf<WordEntity>())
        private set

    var targetWords by mutableStateOf(listOf<WordEntity>())
        private set

    val placedWords = mutableMapOf<WordEntity, List<Pair<Int, Int>>>()

    var highlightedPath by mutableStateOf(emptyList<Pair<Int, Int>>())
        private set

    init {
        newGame()
    }


    fun newGame() {
        viewModelScope.launch {
            try {
                wordRepo.preloadIfEmpty()


                val words = wordRepo.getRandomWords(6)
                    .map { it.copy(word = it.word.trim().uppercase()) }
                    .distinctBy { it.word }

                val tempGrid = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { ' ' } }
                val placed = mutableMapOf<WordEntity, List<Pair<Int, Int>>>()
                val directions = listOf("H", "V")

                for (entity in words) {
                    val word = entity.word
                    val direction = directions.random()
                    val position = findValidPosition(word, direction, tempGrid)

                    if (position != null) {
                        val (x, y) = position
                        placeWord(tempGrid, word, x, y, direction)

                        val path = word.indices.map {
                            when (direction) {
                                "H" -> x to (y + it)
                                "V" -> (x + it) to y
                                else -> x to y
                            }
                        }
                        placed[entity] = path
                    }
                }

                // Rellenar huecos vacíos
                for (i in 0 until GRID_SIZE) {
                    for (j in 0 until GRID_SIZE) {
                        if (tempGrid[i][j] == ' ') {
                            tempGrid[i][j] = randomLetter()
                        }
                    }
                }

                // Actualizar estados
                grid = tempGrid.map { it.toList() }
                placedWords.clear()
                placedWords.putAll(placed)
                targetWords = placed.keys.toList()
                foundWords = emptySet()
                highlightedPath = emptyList()

            } catch (e: Exception) {
                targetWords = emptyList()
                grid = List(GRID_SIZE) { List(GRID_SIZE) { randomLetter() } }
            }
        }
    }

    private fun findValidPosition(word: String, direction: String, grid: List<List<Char>>): Pair<Int, Int>? {
        val maxAttempts = 1000
        val maxX = if (direction == "V") GRID_SIZE - word.length else GRID_SIZE
        val maxY = if (direction == "H") GRID_SIZE - word.length else GRID_SIZE

        repeat(maxAttempts) {
            val x = Random.nextInt(0, maxX)
            val y = Random.nextInt(0, maxY)
            if (canPlaceWord(grid, word, x, y, direction)) {
                return x to y
            }
        }
        return null
    }

    private fun canPlaceWord(grid: List<List<Char>>, word: String, x: Int, y: Int, dir: String): Boolean {
        return word.indices.all {
            val (cx, cy) = when (dir) {
                "H" -> x to (y + it)
                "V" -> (x + it) to y
                else -> x to y
            }
            cx in 0 until GRID_SIZE &&
                    cy in 0 until GRID_SIZE &&
                    (grid[cx][cy] == ' ' || grid[cx][cy] == word[it])
        }
    }

    private fun placeWord(grid: MutableList<MutableList<Char>>, word: String, x: Int, y: Int, dir: String) {
        word.forEachIndexed { i, c ->
            val (cx, cy) = when (dir) {
                "H" -> x to (y + i)
                "V" -> (x + i) to y
                else -> x to y
            }
            grid[cx][cy] = c
        }
    }

    private fun randomLetter(): Char = ('A'..'Z').random()

    fun checkSelectedPath(path: List<Pair<Int, Int>>) {
        val selectedWord = path.map { (x, y) -> grid[x][y] }.joinToString("")
        targetWords.firstOrNull { it.word == selectedWord && it !in foundWords }?.let { word ->
            foundWords = foundWords + word
            highlightedPath = path
            viewModelScope.launch {
                delay(1000)
                highlightedPath = emptyList()
            }
        }
    }
}
