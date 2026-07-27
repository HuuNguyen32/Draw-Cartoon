package nhn.ntech.ndraw.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.domain.repository.ItemRepository
import nhn.ntech.ndraw.domain.state.UiState

class MainViewModel(
    private val itemRepository: ItemRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MainUIState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MainUIState>> = _uiState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                var items = withContext(Dispatchers.IO) {
                    itemRepository.getCategories()
                }

                if (items.isNotEmpty()) {
                    val trending = items.lastOrNull()?.url ?: emptyList()
                    _uiState.value = UiState.Success(MainUIState(trendingList = trending))
                }

                items = withContext(Dispatchers.IO) {
                    itemRepository.fetchCategories()
                }

                val trending = items.lastOrNull()?.url ?: emptyList()
                _uiState.value = UiState.Success(MainUIState(trendingList = trending))
            } catch (e: Exception) {
                val current = _uiState.value
                if (current !is UiState.Success) {
                    _uiState.value = UiState.Error(e.message ?: "Không thể tải dữ liệu")
                }
            }
        }
    }
}
