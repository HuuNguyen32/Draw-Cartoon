package nhn.ntech.ndraw.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import nhn.ntech.ndraw.data.local.entity.ItemEntity

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    suspend fun getAll(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)
}