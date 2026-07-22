package nhn.ntech.ndraw.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
}