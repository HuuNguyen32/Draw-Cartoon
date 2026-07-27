package nhn.ntech.ndraw.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.domain.state.UiState
import nhn.ntech.ndraw.domain.repository.ItemRepository

class CategoryViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CategoryUIState>>(UiState.Loading)
    val uiState: StateFlow<UiState<CategoryUIState>> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                var categories = withContext(Dispatchers.IO) {
                    itemRepository.getCategories()
                }

                if (categories.isNotEmpty()) {
                    val first = categories[0].category
                    _uiState.value = UiState.Success(
                        CategoryUIState(
                            categories = categories,
                            selectedCategory = first,
                            items = categories[0].url
                        )
                    )
                }

                categories = withContext(Dispatchers.IO) {
                    itemRepository.fetchCategories()
                }

                val selected = if (categories.isNotEmpty()) categories[0].category else ""
                val items = categories.firstOrNull()?.url ?: emptyList()

                _uiState.value = UiState.Success(
                    CategoryUIState(
                        categories = categories,
                        selectedCategory = selected,
                        items = items
                    )
                )
            } catch (e: Exception) {
                val current = _uiState.value
                if (current !is UiState.Success) {
                    _uiState.value = UiState.Error(e.message ?: "Không thể tải dữ liệu")
                }
            }
        }
    }

    fun selectCategory(category: String) {
        val current = _uiState.value
        if (current is UiState.Success) {
            val items = current.data.categories
                .firstOrNull { it.category == category }
                ?.url ?: emptyList()

            _uiState.value = UiState.Success(
                current.data.copy(
                    selectedCategory = category,
                    items = items
                )
            )
        }
    }
}