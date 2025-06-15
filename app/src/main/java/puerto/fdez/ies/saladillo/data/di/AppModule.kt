package puerto.fdez.ies.saladillo.data.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import puerto.fdez.ies.saladillo.data.database.WordDatabase
import puerto.fdez.ies.saladillo.data.database.entities.WordDao
import puerto.fdez.ies.saladillo.data.repositories.WordRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordDatabase(app: Application): WordDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            WordDatabase::class.java,
            "words_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }
}

