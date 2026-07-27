package nhn.ntech.ndraw.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nhn.ntech.ndraw.data.local.dao.ItemDao
import nhn.ntech.ndraw.data.local.entity.ItemEntity

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ndraw_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}