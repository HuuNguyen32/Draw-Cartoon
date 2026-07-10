package nhn.ntech.ndraw.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class LanguageViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
            return LanguageViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}