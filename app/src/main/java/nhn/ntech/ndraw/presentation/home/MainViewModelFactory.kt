package nhn.ntech.ndraw.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.domain.repository.ItemRepository

class MainViewModelFactory(
    private val itemRepository: ItemRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(itemRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}