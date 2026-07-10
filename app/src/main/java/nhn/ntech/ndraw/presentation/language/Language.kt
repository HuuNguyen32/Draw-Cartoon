package nhn.ntech.ndraw.presentation.language

import androidx.annotation.DrawableRes

data class Language(
    val code: String,
    val name: String,
    @field:DrawableRes val flagRes: Int
)
