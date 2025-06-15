package puerto.fdez.ies.saladillo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import puerto.fdez.ies.saladillo.data.database.entities.WordDao
import puerto.fdez.ies.saladillo.data.database.entities.WordEntity

@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

}
