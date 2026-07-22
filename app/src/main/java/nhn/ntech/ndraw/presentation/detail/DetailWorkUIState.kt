package nhn.ntech.ndraw.presentation.detail

import nhn.ntech.ndraw.consts.Const

data class DetailWorkUIState(
    val counterTime: String = Const.DEFAULT_TIME,
    val currentProgress: Int = 0,
)
