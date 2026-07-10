package nhn.ntech.ndraw.presentation.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    fun setPermissionGranted(granted: Boolean) {
        _isPermissionGranted.value = granted
    }
}