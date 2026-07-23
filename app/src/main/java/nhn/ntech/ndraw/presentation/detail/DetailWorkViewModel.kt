package nhn.ntech.ndraw.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DetailWorkViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DetailWorkUIState())
    val uiState: StateFlow<DetailWorkUIState> = _uiState.asStateFlow()

    fun updateCounterTime(time: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(counterTime = time)
            }
        }
    }

    fun updateCurrentProgress(progress: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(currentProgress = progress)
            }
        }
    }

    fun deleteFile(file: File, onCompletion: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (file.exists()) {
                    file.delete()
                }
            }
            onCompletion()
        }
    }
}