package nhn.ntech.ndraw.presentation.sketching

import androidx.camera.core.CameraSelector

data class SketchingUIState(
    val isFlipHorizontal: Boolean = false,
    val isFlipCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val flashMode: FlashMode = FlashMode.OFF,
    val captureMode: CaptureMode = CaptureMode.PHOTO,

    val isStickerLocked: Boolean = false,
    val isSeekBarOpacityVisible: Boolean = false,
    val stickerOpacity: Float = 0.5f,
    val isGuideVisible: Boolean = false,

    val isCapturing: Boolean = false,
    val isRecording: Boolean = false,
    val recordingTime: String = "00:00"
)