package puerto.fdez.ies.saladillo.data.database.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): WordEntity

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun countWords(): Int

    @Query("SELECT * FROM words WHERE word = :text")
    suspend fun getWordByText(text: String): WordEntity?

    @Query("SELECT definition FROM words WHERE word = :word")
    suspend fun getDefinitionFor(word: String): String?
}

