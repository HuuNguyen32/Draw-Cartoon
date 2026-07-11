package nhn.ntech.ndraw.presentation.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel : ViewModel() {
    private var _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    fun loadCategories(categories: List<String>) {
        _categories.value = categories
    }
}