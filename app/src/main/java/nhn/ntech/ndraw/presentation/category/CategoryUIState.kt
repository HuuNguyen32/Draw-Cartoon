package nhn.ntech.ndraw.presentation.category

import nhn.ntech.ndraw.domain.model.ItemModel

data class CategoryUIState(
    val categories: List<ItemModel> = emptyList(),
    val selectedCategory: String = "",
    val items: List<String> = emptyList(),
)
