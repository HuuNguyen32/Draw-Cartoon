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
        _uiState.update { state ->
            state.copy(isCateMode = cateMode)
        }
        refreshData(filesDir)
    }

    fun loadListFile(filesDir: File) {
        viewModelScope.launch {
            val photos = withContext(Dispatchers.IO) {
                val dir = File(filesDir, Const.MY_WORKS_FOLDER)
                if (dir.exists()) {
                    dir.listFiles { file -> file.extension in listOf("jpg", "jpeg", "png", "webp") }
                        ?.sortedByDescending { it.lastModified() }
                        ?: emptyList()
                } else {
                    emptyList()
                }
            }
            _uiState.update { state ->
                state.copy(listFile = photos)
            }
        }
    }

    fun loadListVideo(filesDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = withContext(Dispatchers.IO) {
                val dir = File(filesDir, Const.MY_WORKS_FOLDER)
                if (dir.exists()) {
                    dir.listFiles { file -> file.extension in listOf("mp4", "mkv", "avi") }
                        ?.sortedByDescending { it.lastModified() }
                        ?: emptyList()
                } else emptyList()
            }
            _uiState.update { state ->
                state.copy(listVideo = videos)
            }
        }
    }

    fun refreshData(filesDir: File) {
        when (uiState.value.isCateMode) {
            CateMode.PHOTO -> loadListFile(filesDir)
            CateMode.VIDEO -> loadListVideo(filesDir)
        }
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
}