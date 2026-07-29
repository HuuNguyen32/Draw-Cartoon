package nhn.ntech.ndraw.presentation.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.consts.Const
import java.io.File

class MyWorkViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyWorkUIState())
    val uiState: StateFlow<MyWorkUIState> = _uiState.asStateFlow()

    fun updateCateMode(cateMode: CateMode, filesDir: File) {
        _uiState.update { state -> state.copy(isCateMode = cateMode) }
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                val dir = File(filesDir, Const.MY_WORKS_FOLDER)
                if (dir.exists()) {
                    val extensions = if (cateMode == CateMode.PHOTO) {
                        listOf("jpg", "jpeg", "png", "webp")
                    } else {
                        listOf("mp4", "mkv", "avi")
                    }
                    dir.listFiles { file -> file.extension in extensions }
                        ?.sortedByDescending { it.lastModified() }
                        ?: emptyList()
                } else emptyList()
            }
            _uiState.update { state ->
                if (cateMode == CateMode.PHOTO) {
                    state.copy(isLoading = false, listFile = list)
                } else {
                    state.copy(isLoading = false, listVideo = list)
                }
            }
        }
    }

    fun refreshData(filesDir: File) {
        updateCateMode(uiState.value.isCateMode, filesDir)
    }

    fun deleteFiles(filesToDelete: List<File>, filesDir: File, onCompletion: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filesToDelete.forEach { file ->
                    if (file.exists())
                        file.delete()
                }
            }
            refreshData(filesDir)
            onCompletion()
        }
    }

    fun toggleSelectMore() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isSelectMore = !state.isSelectMore)
            }
        }
    }
}