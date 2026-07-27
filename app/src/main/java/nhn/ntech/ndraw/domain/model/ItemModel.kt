package nhn.ntech.ndraw.domain.model

data class ItemModel(
    val id: Int = 0,
    val category: String = "",
    val url: List<String> = emptyList(),
)
