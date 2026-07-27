package nhn.ntech.ndraw.presentation.sketching

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivitySketchingBinding
import nhn.ntech.ndraw.ext.setTextGradientColor
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.utils.DialogUtils
import nhn.ntech.ndraw.ext.clearTextShader
import nhn.ntech.ndraw.presentation.work.MyWorkActivity
import java.io.File

class SketchingActivity : BaseActivity() {

    private lateinit var binding: ActivitySketchingBinding
    private lateinit var viewModel: SketchingViewModel
    private lateinit var photoUri: Uri
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isCameraSelector: CameraSelector? = null
    private var currentCaptureMode: CaptureMode = CaptureMode.PHOTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySketchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        viewModel = ViewModelProvider(this)[SketchingViewModel::class.java]
        photoUri = intent.getStringExtra(Const.IMAGE_URI_TAG)?.toUri() ?: Uri.EMPTY
        initView()
        setOnListeners()
        observeState()
        startCamera()
    }

    private fun startCamera(cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }

            try {
                cameraProvider.unbindAll()

                if (currentCaptureMode == CaptureMode.PHOTO) {
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } else {
                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build()
                    videoCapture = VideoCapture.withOutput(recorder)
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
                }
            } catch (e: Exception) {
                Log.e("CameraX", "Failed to bind camera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val dir = File(filesDir, Const.MY_WORKS_FOLDER)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "ndraw_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    DialogUtils.createConfirmDialog(
                        this@SketchingActivity,
                        getString(R.string.saved_photo_title),
                        getString(R.string.saved_photo_des),
                        getString(R.string.view_title),
                        getString(R.string.close_title),
                        onConfirm = {
                            val intent = Intent(this@SketchingActivity, MyWorkActivity::class.java)
                            intent.putExtra(Const.FROM_SKETCHING, true)
                            intent.putExtra(Const.IS_PHOTO_FROM_SKETCHING, true)
                            startActivity(intent)
                            finish()
                        }
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed", exception)
                }
            }
        )
    }

    @SuppressLint("DefaultLocale")
    private fun recordVideo() {
        val videoCapture = this.videoCapture ?: return

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val dir = File(filesDir, Const.MY_WORKS_FOLDER)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "ndraw_${System.currentTimeMillis()}.mp4")

        val outputOptions = FileOutputOptions.Builder(file).build()

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        viewModel.updateRecording(true)
                        viewModel.updateRecordingTime("00:00")
                    }

                    is VideoRecordEvent.Status -> {
                        val timeNs = recordEvent.recordingStats.recordedDurationNanos
                        val seconds = (timeNs / 1_000_000_000).toInt()
                        val minutes = seconds / 60
                        val displaySeconds = seconds % 60
                        val timeString = String.format("%02d:%02d", minutes, displaySeconds)
                        viewModel.updateRecordingTime(timeString)
                    }

                    is VideoRecordEvent.Finalize -> {
                        viewModel.updateRecording(false)
                        viewModel.updateRecordingTime("00:00")
                        if (!recordEvent.hasError()) {
                            DialogUtils.createConfirmDialog(
                                this@SketchingActivity,
                                getString(R.string.saved_video_title),
                                getString(R.string.saved_video_des),
                                getString(R.string.view_title),
                                getString(R.string.close_title),
                                onConfirm = {
                                    val intent =
                                        Intent(this@SketchingActivity, MyWorkActivity::class.java)
                                    intent.putExtra(Const.FROM_SKETCHING, true)
                                    intent.putExtra(Const.IS_PHOTO_FROM_SKETCHING, false)
                                    startActivity(intent)
                                    finish()
                                }
                            )
                        } else {
                            recording?.close()
                            recording = null
                            Log.e("CameraX", "Video capture ends with error: ${recordEvent.error}")
                        }
                    }
                }
            }
    }

    private fun render(state: SketchingUIState) = with(binding) {
        ivSticker.isLocked = state.isStickerLocked
        ivSticker.alpha = state.stickerOpacity

        if (state.isFlipHorizontal) {
            ivSticker.flipHorizontal()
            viewModel.toggleFlip()
        }

        sbOpacity.visibility = if (state.isSeekBarOpacityVisible) View.VISIBLE else View.GONE
        val progressFromState = (state.stickerOpacity * 100).toInt()
        if (sbOpacity.progress != progressFromState) {
            sbOpacity.progress = progressFromState
        }

        val barVisibility = if (state.isStickerLocked) View.GONE else View.VISIBLE
        topBarContainer.visibility = barVisibility
        bottomBarContainer.visibility = barVisibility
        btnUnlock.visibility = if (state.isStickerLocked) View.VISIBLE else View.GONE

        // Apply flash mode
        imageCapture?.flashMode = when (state.flashMode) {
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
            FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
        }
        btnFlash.setImageResource(
            if (state.flashMode == FlashMode.ON) R.drawable.ic_flash
            else R.drawable.ic_no_flash
        )

        when (state.captureMode) {
            CaptureMode.PHOTO -> {
                tvTogglePhoto.visibility = View.GONE
                tvVideo.visibility = View.VISIBLE
                tvPhoto.text = getString(R.string.photo_title)
                btnAction.setImageResource(R.drawable.ic_button_photo)
            }

            CaptureMode.VIDEO -> {
                tvTogglePhoto.visibility = if (state.isRecording) View.GONE else View.VISIBLE
                tvVideo.visibility = View.GONE
                tvPhoto.apply {
                    when {
                        state.isRecording -> {
                            text = state.recordingTime
                            clearTextShader()
                            setTextColor(
                                ContextCompat.getColor(
                                    this@SketchingActivity,
                                    R.color.bright_red
                                )
                            )
                        }

                        else -> {
                            text = getString(R.string.video_title)
                            setTextGradientColor()
                        }
                    }
                }
                btnAction.setImageResource(if (state.isRecording) R.drawable.ic_button_record else R.drawable.ic_button_camera)
            }
        }

        if (currentCaptureMode != state.captureMode) {
            currentCaptureMode = state.captureMode
            startCamera(state.isFlipCamera)
        } else if (isCameraSelector != state.isFlipCamera) {
            isCameraSelector = state.isFlipCamera
            startCamera(state.isFlipCamera)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }
    }

    private fun setOnListeners() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
            btnSwap.setOnClickListener { viewModel.toggleFlip() }
            btnOpacity.setOnClickListener { viewModel.toggleOpacity() }
            btnGuide.setOnClickListener { DialogUtils.createInstructionDialog(this@SketchingActivity) }
            btnLock.setOnClickListener { viewModel.toggleLock() }
            btnUnlock.setOnClickListener { viewModel.toggleLock() }
            btnFlash.setOnClickListener { viewModel.toggleFlashMode() }
            btnAction.setOnClickListener {
                if (currentCaptureMode == CaptureMode.PHOTO) {
                    takePhoto()
                } else {
                    recordVideo()
                }
            }

            sbOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        viewModel.updateOpacity((p1.toFloat() / 100))
                        Log.d("Opacity", "${p1.toFloat() / 100}")
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
            tvVideo.setOnClickListener {
                viewModel.updateCaptureMode(CaptureMode.VIDEO)
            }

            tvTogglePhoto.setOnClickListener {
                viewModel.updateCaptureMode(CaptureMode.PHOTO)
            }
            btnFlipCamera.setOnClickListener { viewModel.toggleFlipCamera() }
        }
    }

    private fun initView() {
        with(binding) {
            tvPhoto.setTextGradientColor()

            Glide.with(this@SketchingActivity)
                .load(photoUri)
                .into(ivSticker)

            ivSticker.alpha = 0.5f

            sbOpacity.progress = 50
        }
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }
}