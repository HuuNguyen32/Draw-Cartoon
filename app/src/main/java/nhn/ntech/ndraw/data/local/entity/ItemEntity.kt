package nhn.ntech.ndraw.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "url")
    val url: List<String>,
)