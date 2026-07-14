package nhn.ntech.ndraw.presentation.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private val _isCameraPermissionGranted = MutableLiveData<Boolean>()
    val isCameraPermissionGranted: LiveData<Boolean> = _isCameraPermissionGranted
    fun setPermissionGranted(granted: Boolean) {
        _isPermissionGranted.value = granted
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _isCameraPermissionGranted.value = granted
    }
}