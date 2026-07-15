package nhn.ntech.ndraw.presentation.sketching

data class SketchingUIState(
    val isFlipHorizontal: Boolean = false,
    val flashMode: FlashMode = FlashMode.OFF,
    val captureMode: CaptureMode = CaptureMode.PHOTO,

    val isStickerLocked: Boolean = false,
    val isSeekBarOpacityVisible: Boolean = false,
    val stickerOpacity: Float = 0.5f,
    val isGuideVisible: Boolean = false,

    val isCapturing: Boolean = false,
)