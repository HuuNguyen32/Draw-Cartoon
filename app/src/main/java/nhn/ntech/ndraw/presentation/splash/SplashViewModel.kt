package nhn.ntech.ndraw.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _isLanguageSet = MutableStateFlow<String?>(null)
    val isLanguageSet: StateFlow<String?> = _isLanguageSet.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val language = userPreferences.getLanguage()
            _isLanguageSet.value = language ?: ""
        }
    }
}