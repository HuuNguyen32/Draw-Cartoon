package nhn.ntech.ndraw.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.presentation.language.LanguageViewModel

class SplashViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}