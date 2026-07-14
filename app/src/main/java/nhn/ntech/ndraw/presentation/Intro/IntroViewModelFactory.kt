package nhn.ntech.ndraw.presentation.Intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class IntroViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            return IntroViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}