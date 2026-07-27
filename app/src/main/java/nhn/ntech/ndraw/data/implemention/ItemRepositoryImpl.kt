package nhn.ntech.ndraw.data.implemention

import nhn.ntech.ndraw.data.api.RetrofitClient
import nhn.ntech.ndraw.data.local.AppDatabase
import nhn.ntech.ndraw.domain.model.ItemModel
import nhn.ntech.ndraw.domain.repository.ItemRepository
import nhn.ntech.ndraw.mapper.toListItemDTO
import nhn.ntech.ndraw.mapper.toListItemEntity
import nhn.ntech.ndraw.mapper.toListItemModel

class ItemRepositoryImpl(
    private val db: AppDatabase,
) : ItemRepository {
    private val api = RetrofitClient.instance
    private val itemDao = db.getItemDao()
    override suspend fun fetchCategories(): List<ItemModel> {
        val response = api.getCategories().toListItemDTO()
        val items = response.toListItemEntity()
        itemDao.insertAll(items)
        return items.toListItemModel()
    }

    override suspend fun getCategories(): List<ItemModel> {
        return itemDao.getAll().toListItemModel()
    }


}