package by.android.academy.minsk.fastfinger.score

import android.content.Context
import androidx.room.*

const val LOCAL_BEST_SCORE_ID = 0

@Entity(tableName = "scores")
data class ScoreEntity(val value: Int, @PrimaryKey val id: Int)

//TODO(5): coroutines integration
@Dao
abstract class ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun updateScore(score: ScoreEntity)

    @Query("select value from scores where id = :id")
    protected abstract fun getScore(id: Int): Int?

    fun updateLocalBestScore(value: Int) {
        updateScore(ScoreEntity(value, LOCAL_BEST_SCORE_ID))
    }

    fun getLocalBestScore(): Int? = getScore(LOCAL_BEST_SCORE_ID)
}

@Database(entities = [ScoreEntity::class], version = 1, exportSchema = false)
abstract class ScoreDb : RoomDatabase() {
    abstract val scoreDao: ScoreDao
}

private lateinit var INSTANCE: ScoreDb

fun getDatabase(context: Context): ScoreDb {
    synchronized(ScoreDb::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    ScoreDb::class.java,
                    "scores_db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}