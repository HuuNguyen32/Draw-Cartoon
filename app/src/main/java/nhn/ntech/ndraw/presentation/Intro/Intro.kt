package nhn.ntech.ndraw.presentation.Intro

import androidx.annotation.DrawableRes

data class Intro(
    @field:DrawableRes val imgRes: Int,
    val title: String? = null
)
