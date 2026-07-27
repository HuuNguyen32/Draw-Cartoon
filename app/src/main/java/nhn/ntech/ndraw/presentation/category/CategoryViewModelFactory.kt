package nhn.ntech.ndraw.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.domain.repository.ItemRepository

class CategoryViewModelFactory(
    private val itemRepository: ItemRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java))
            return CategoryViewModel(itemRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
