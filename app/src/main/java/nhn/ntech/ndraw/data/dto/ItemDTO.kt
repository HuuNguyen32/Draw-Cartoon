package nhn.ntech.ndraw.data.dto

import com.google.gson.annotations.SerializedName

data class ItemDTO(
    @SerializedName("category")
    val category: String = "",

    @SerializedName("quantity")
    val quantity: Int = 0,
)
