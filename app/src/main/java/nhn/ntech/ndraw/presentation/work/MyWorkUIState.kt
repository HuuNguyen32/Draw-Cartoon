package nhn.ntech.ndraw.presentation.work

import java.io.File

data class MyWorkUIState(
    val isLoading: Boolean = false,
    val isCateMode: CateMode = CateMode.PHOTO,
    val listFile: List<File> = emptyList(),
    val listVideo: List<File> = emptyList()
)
