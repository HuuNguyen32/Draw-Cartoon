package nhn.ntech.ndraw.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel(){
    private var _isLanguageSet = MutableLiveData<String>()
    val isLanguageSet: LiveData<String> = _isLanguageSet

    fun loadData() {
        _isLanguageSet.value = userPreferences.getLanguage()
    }
}