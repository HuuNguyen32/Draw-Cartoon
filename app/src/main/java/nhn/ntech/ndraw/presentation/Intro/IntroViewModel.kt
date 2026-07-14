package nhn.ntech.ndraw.presentation.Intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.domain.prefs.UserPreferences

class IntroViewModel(
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _introList = MutableLiveData<List<Intro>>()
    val introList: LiveData<List<Intro>> = _introList

    fun loadIntroList(intros: List<Intro>) {
        _introList.value = intros
    }

    fun getPermissionVisited(): Boolean = userPreferences.getPermissionScreenVisited()
    fun setPermissionVisited(isVisited: Boolean) {
        userPreferences.isPermissionScreenVisited(isVisited)
    }
}