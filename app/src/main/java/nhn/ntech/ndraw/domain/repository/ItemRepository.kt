package nhn.ntech.ndraw.domain.repository

import nhn.ntech.ndraw.data.local.entity.ItemEntity
import nhn.ntech.ndraw.domain.model.ItemModel

interface ItemRepository {
    suspend fun fetchCategories(): List<ItemModel>
    suspend fun getCategories(): List<ItemModel>
}