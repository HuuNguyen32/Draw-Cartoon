package nhn.ntech.ndraw.presentation.Intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nhn.ntech.ndraw.R

class IntroViewModel : ViewModel() {
    private val _introList = MutableLiveData<List<Intro>>()
    val introList: LiveData<List<Intro>> = _introList

    fun loadIntroList(intros: List<Intro>) {
        _introList.value = intros
    }
}