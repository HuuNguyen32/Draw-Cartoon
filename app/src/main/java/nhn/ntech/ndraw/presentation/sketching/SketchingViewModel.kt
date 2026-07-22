package nhn.ntech.ndraw.presentation.sketching

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SketchingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SketchingUIState>(SketchingUIState())
    val uiState: StateFlow<SketchingUIState> = _uiState.asStateFlow()

    fun toggleFlip() {
        _uiState.update { state ->
            state.copy(isFlipHorizontal = !state.isFlipHorizontal)
        }
    }

    fun toggleFlashMode() {
        _uiState.update { state ->
            val newFlashMode = when (state.flashMode) {
                FlashMode.ON -> FlashMode.OFF
                FlashMode.OFF -> FlashMode.ON
                else -> FlashMode.OFF
            }
            state.copy(flashMode = newFlashMode)
        }
    }

    fun updateCaptureMode(captureMode: CaptureMode) {
        _uiState.update {
            it.copy(captureMode = captureMode)
        }
    }

    fun toggleLock() {
        _uiState.update { state ->
            state.copy(isStickerLocked = !state.isStickerLocked)
        }
    }

    fun toggleOpacity() {
        _uiState.update { state ->
            state.copy(isSeekBarOpacityVisible = !state.isSeekBarOpacityVisible)
        }
    }

    fun updateOpacity(opacity: Float) {
        _uiState.update { state ->
            state.copy(stickerOpacity = opacity)
        }
    }

    fun toggleGuide() {
        _uiState.update { state ->
            state.copy(isGuideVisible = !state.isGuideVisible)
        }
    }

    fun updateCapturing(isCapturing: Boolean) {
        _uiState.update { state ->
            state.copy(isCapturing = isCapturing)
        }
    }

    fun updateRecording(isRecording: Boolean) {
        _uiState.update { state ->
            state.copy(isRecording = isRecording)
        }
    }

    fun updateRecordingTime(time: String) {
        _uiState.update { state ->
            state.copy(recordingTime = time)
        }
    }

    fun toggleFlipCamera() {
        _uiState.update { state ->
            val newLen = if (state.isFlipCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            state.copy(isFlipCamera = newLen)
        }
    }
}