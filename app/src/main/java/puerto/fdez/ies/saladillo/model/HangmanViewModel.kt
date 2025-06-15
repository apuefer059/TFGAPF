package puerto.fdez.ies.saladillo.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import puerto.fdez.ies.saladillo.data.repositories.WordRepository
import javax.inject.Inject

@HiltViewModel
class HangmanViewModel @Inject constructor(private val wordRepo: WordRepository) : ViewModel() {

    private val maxMistakes = 6

    private val _secretWord = MutableStateFlow("")
    val secretWord: StateFlow<String> = _secretWord

    private val _definition = MutableStateFlow("")
    val definition: StateFlow<String> = _definition


    private val _hint = MutableStateFlow("")
    val hint: StateFlow<String> = _hint

    private val _guessedLetters = MutableStateFlow(setOf<Char>())
    val guessedLetters: StateFlow<Set<Char>> = _guessedLetters

    private val _mistakes = MutableStateFlow(0)
    val mistakes: StateFlow<Int> = _mistakes

    private val _gameOver = MutableStateFlow(false)
    val gameOver: StateFlow<Boolean> = _gameOver

    private val _isWon = MutableStateFlow(false)
    val isWon: StateFlow<Boolean> = _isWon

    private val _displayWord = MutableStateFlow("")
    val displayWord: StateFlow<String> = _displayWord

    init {
        viewModelScope.launch {
            wordRepo.preloadIfEmpty() // Por si no hay palabras
            startNewGame()
        }
    }

    fun startNewGame() {
        viewModelScope.launch {
            val wordEntity = wordRepo.getRandomWord()
            _secretWord.value = wordEntity.word.uppercase()
            _definition.value = wordEntity.definition
            _hint.value = wordEntity.hint
            _guessedLetters.value = emptySet()
            _mistakes.value = 0
            _gameOver.value = false
            _isWon.value = false
            updateDisplayWord()
        }
    }

    fun guessLetter(letter: Char) {
        if (_gameOver.value) return

        val normalizedLetter = letter.uppercaseChar()
        if (_guessedLetters.value.contains(normalizedLetter)) return

        _guessedLetters.value = _guessedLetters.value + normalizedLetter

        if (!_secretWord.value.contains(normalizedLetter)) {
            _mistakes.value += 1
            if (_mistakes.value >= maxMistakes) {
                _gameOver.value = true
                _isWon.value = false
            }
        } else {
            val allLettersGuessed = _secretWord.value.toSet().all { _guessedLetters.value.contains(it) }
            if (allLettersGuessed) {
                _gameOver.value = true
                _isWon.value = true
            }
        }
        updateDisplayWord()
    }

    private fun updateDisplayWord() {
        _displayWord.value = _secretWord.value.map {
            if (_guessedLetters.value.contains(it)) it else '_'
        }.joinToString(" ")
    }
}


