package nhn.ntech.ndraw.presentation.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nhn.ntech.ndraw.utils.LanguageUtils
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class LanguageViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _languageList = MutableLiveData<List<Language>>()
    val languageList: LiveData<List<Language>> = _languageList

    private val _language = MutableLiveData<Language>()
    val language: LiveData<Language> = _language

    fun loadLanguageList() {
        val languageList = LanguageUtils.listLanguage
        _languageList.value = languageList
    }

    fun saveLanguage(language: Language) {
        userPreferences.saveLanguage(language.code)
    }

    fun setLanguage(language: Language) {
        _language.value = language
    }
}
