package nhn.ntech.ndraw.presentation.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityDetailWorkBinding
import nhn.ntech.ndraw.helper.ExoPlayerHelper
import nhn.ntech.ndraw.utils.TransferUtils
import java.io.File

class DetailWorkActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailWorkBinding
    private val exoPlayerHelper by lazy { ExoPlayerHelper(this) }
    private val viewModel by lazy { DetailWorkViewModel() }
    private var file: File? = null
    private var isPhoto = true
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val currentPos = exoPlayerHelper.getCurrentPosition()

            if (exoPlayerHelper.isPlaying()) {
                viewModel.updateCurrentProgress(currentPos.toInt())
                viewModel.updateCounterTime(TransferUtils.formatDuration(currentPos))
            }
            handler.postDelayed(this, 250)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        getValueByIntent()
        initView()
        setOnListener()
        observeState()
    }

    private fun render(state: DetailWorkUIState) = with(binding) {
        tvCounterTime.text = state.counterTime
        binding.sbVideo.progress = state.currentProgress
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }
    }

    private fun getValueByIntent() {
        file = File(intent.getStringExtra(Const.FILE_TAG) ?: "")
        isPhoto = intent.getBooleanExtra(Const.IS_PHOTO_TAG, true)
    }

    private fun setOnListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
            btnPlay.setOnClickListener {
                if (exoPlayerHelper.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_gradient_play)
                    exoPlayerHelper.pause()
                } else {
                    if (exoPlayerHelper.getPLayer()?.playbackState == Player.STATE_ENDED || binding.sbVideo.progress == binding.sbVideo.max) {
                        exoPlayerHelper.seekTo(0)
                        viewModel.updateCounterTime(getString(R.string.start_time))
                    }
                    btnPlay.setImageResource(R.drawable.ic_gradient_pause)
                    exoPlayerHelper.play()
                }
            }

            sbVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    handler.removeCallbacks(updateProgressRunnable)
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (p0 != null) {
                        val pos = p0.progress
                        exoPlayerHelper.seekTo(pos.toLong())
                        viewModel.updateCurrentProgress(pos)
                        viewModel.updateCounterTime(TransferUtils.formatDuration(pos.toLong()))
                    }
                    handler.post(updateProgressRunnable)
                }
            })

            btnNext.setOnClickListener {
                val currentPos = exoPlayerHelper.getCurrentPosition()
                val nextPos = currentPos + 500
                if (nextPos > exoPlayerHelper.getDuration()) return@setOnClickListener
                exoPlayerHelper.seekTo(nextPos)
                viewModel.updateCurrentProgress(nextPos.toInt())
                viewModel.updateCounterTime(TransferUtils.formatDuration(nextPos))
            }

            btnPrevious.setOnClickListener {
                val currentPos = exoPlayerHelper.getCurrentPosition()
                val previousPos = currentPos - 500
                if (previousPos < 0) return@setOnClickListener
                exoPlayerHelper.seekTo(previousPos)
                viewModel.updateCurrentProgress(previousPos.toInt())
                viewModel.updateCounterTime(TransferUtils.formatDuration(previousPos))
            }
        }
    }

    private fun initView() = with(binding) {
        if (isPhoto) {
            tvTitle.text = getString(R.string.photo_title)
            controllerContainer.visibility = View.GONE
            progressContainer.visibility = View.GONE
            pvVideo.visibility = View.GONE
            ivPhoto.visibility = View.VISIBLE
            Glide.with(this@DetailWorkActivity)
                .load(file?.toUri())
                .into(ivPhoto)
        } else {
            tvTitle.text = getString(R.string.video_title)
            controllerContainer.visibility = View.VISIBLE
            progressContainer.visibility = View.VISIBLE
            initVideo()
        }
    }

    private fun initVideo() {
        exoPlayerHelper.initPlayer(binding.pvVideo)
        val uri = file?.toUri() ?: "".toUri()
        exoPlayerHelper.setMedia(uri)
        exoPlayerHelper.getPLayer()?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = exoPlayerHelper.getDuration()
                    if (exoPlayerHelper.isPlaying()) {
                        binding.btnPlay.setImageResource(R.drawable.ic_gradient_pause)
                    }
                    if (duration != C.TIME_UNSET) {
                        binding.sbVideo.max = duration.toInt()
                        binding.tvTotalTime.text = TransferUtils.formatDuration(duration)
                        handler.removeCallbacks(updateProgressRunnable)
                        handler.post(updateProgressRunnable)
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    binding.btnPlay.setImageResource(R.drawable.ic_gradient_play)
                    binding.sbVideo.progress = binding.sbVideo.max
                    handler.removeCallbacks(updateProgressRunnable)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        exoPlayerHelper.release()
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}