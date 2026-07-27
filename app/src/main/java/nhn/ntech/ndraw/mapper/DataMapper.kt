package nhn.ntech.ndraw.mapper

import nhn.ntech.ndraw.data.dto.ItemDTO
import nhn.ntech.ndraw.data.local.entity.ItemEntity
import nhn.ntech.ndraw.domain.model.ItemModel

private const val BASE_URL = "https://lvtglobal.site/public/app/ST114_DrawCartoonARDrawing/ar/"

fun Map<String, List<ItemDTO>>.toListItemDTO(): List<ItemDTO> {
    return this.flatMap { mapEntry ->
        mapEntry.value.map { itemDTO ->
            ItemDTO(
                category = mapEntry.key,
                quantity = itemDTO.quantity
            )
        }
    }
}

fun List<ItemDTO>.toListItemEntity(): List<ItemEntity> {
    return this.map {
        val urls = (1..it.quantity).map { index -> BASE_URL + it.category + "/$index.jpg" }
        ItemEntity(
            category = it.category,
            url = urls
        )
    }
}

fun ItemEntity.toModel(): ItemModel {
    return ItemModel(
        id = this.id,
        category = this.category,
        url = this.url
    )
}

fun ItemModel.toEntity(): ItemEntity {
    return ItemEntity(
        id = this.id,
        category = this.category,
        url = this.url
    )
}

fun List<ItemEntity>.toListItemModel(): List<ItemModel> {
    return this.map { it.toModel() }
}
