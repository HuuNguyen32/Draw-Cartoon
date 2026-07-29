package nhn.ntech.ndraw.utils

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import kotlinx.coroutines.Runnable
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ConfirmDialogBinding
import nhn.ntech.ndraw.databinding.DoubleButtonDialogBinding
import nhn.ntech.ndraw.databinding.InstructionDialogBinding
import nhn.ntech.ndraw.databinding.RateDialogBinding
import nhn.ntech.ndraw.ext.setTextGradientColor
import nhn.ntech.ndraw.helper.ExoPlayerHelper

class DialogUtils {
    companion object {
        private fun applyImmersiveMode(dialog: Dialog) {
            dialog.window?.let { window ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.let { controller ->
                        controller.hide(WindowInsets.Type.navigationBars())
                        controller.systemBarsBehavior =
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            )
                }
            }
        }

        fun createRateDialog(context: Context, onRate: (Int) -> Unit) {
            val dialog = Dialog(context)
            val binding = RateDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rate_dialog_bg)
            dialog.setCanceledOnTouchOutside(true)
            with(binding) {
                ivRate.setImageResource(R.drawable.img_rate_0)
                tvTitle.text = context.getString(R.string.no_star_title)
                tvDes.text = context.getString(R.string.no_star_des)
                rbRate.isClearRatingEnabled = false
                rbRate.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                    if (fromUser) {
                        when (rating.toInt()) {
                            1 -> {
                                ivRate.setImageResource(R.drawable.img_rate_1)
                                tvTitle.text = context.getString(R.string.one_to_three_star_title)
                                tvDes.text = context.getString(R.string.one_to_three_star_des)
                            }

                            2 -> {
                                ivRate.setImageResource(R.drawable.img_rate_2)
                                tvTitle.text = context.getString(R.string.one_to_three_star_title)
                                tvDes.text = context.getString(R.string.one_to_three_star_des)
                            }

                            3 -> {
                                ivRate.setImageResource(R.drawable.img_rate_3)
                                tvTitle.text = context.getString(R.string.one_to_three_star_title)
                                tvDes.text = context.getString(R.string.one_to_three_star_des)
                            }

                            4 -> {
                                ivRate.setImageResource(R.drawable.img_rate_4)
                                tvTitle.text = context.getString(R.string.four_five_start_title)
                                tvDes.text = context.getString(R.string.four_five_start_des)
                            }

                            5 -> {
                                ivRate.setImageResource(R.drawable.img_rate_5)
                                tvTitle.text = context.getString(R.string.four_five_start_title)
                                tvDes.text = context.getString(R.string.four_five_start_des)
                            }

                            else -> {
                                ivRate.setImageResource(R.drawable.img_rate_0)
                                tvTitle.text = context.getString(R.string.no_star_title)
                                tvDes.text = context.getString(R.string.no_star_des)
                            }
                        }
                    }
                }
                btnRate.setOnClickListener {
                    val rate = rbRate.rating.toInt()
                    onRate(rate)
                    if (rate != 0) dialog.dismiss()
                }

                btnExit.setOnClickListener {
                    dialog.dismiss()
                }
            }
            dialog.show()
            applyImmersiveMode(dialog)
        }

        fun createDrawDialog(context: Context, fromCamera: () -> Unit, fromGallery: () -> Unit) {
            val dialog = Dialog(context)
            val binding = DoubleButtonDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawableResource(R.drawable.white_bg)
            dialog.setCanceledOnTouchOutside(true)
            with(binding) {
                tvTitle.text = context.getString(R.string.create_draw_title)
                btnFromCamera.setOnClickListener {
                    dialog.dismiss()
                    fromCamera()
                }
                btnFromGallery.setOnClickListener {
                    dialog.dismiss()
                    fromGallery()
                }
            }
            dialog.show()
            applyImmersiveMode(dialog)
        }

        fun createConfirmDialog(
            context: Context,
            title: String,
            des: String,
            positiveButton: String = context.getString(R.string.yes_title),
            negativeButton: String = context.getString(R.string.no_title),
            onConfirm: () -> Unit,
            onDeny: () -> Unit = {},
            isSketching: Boolean = false,
        ): Dialog {
            val dialog = Dialog(context)
            val binding = ConfirmDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawableResource(R.drawable.white_bg)
            dialog.setCanceledOnTouchOutside(false)
            with(binding) {
                tvTitle.text = title
                tvDes.text = des
                btnYes.text = positiveButton
                btnNo.text = negativeButton
                btnNo.setTextGradientColor()
                btnNo.setOnClickListener {
                    dialog.dismiss()
                    if (isSketching) onDeny()
                }
                btnYes.setOnClickListener {
                    dialog.dismiss()
                    onConfirm()
                }
            }
            dialog.show()
            applyImmersiveMode(dialog)
            return dialog
        }

        fun createInstructionDialog(context: Context, lifecycleOwner: LifecycleOwner) {
            val dialog = Dialog(context)
            val binding = InstructionDialogBinding.inflate(LayoutInflater.from(context))
            val exoPlayerHelper = ExoPlayerHelper(context)

            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawableResource(R.drawable.white_bg)
            dialog.setCanceledOnTouchOutside(false)

            val handler = Handler(Looper.getMainLooper())
            val updateProgressRunnable = object : Runnable {
                override fun run() {
                    if (exoPlayerHelper.isPlaying()) {
                        binding.sbVideo.progress = exoPlayerHelper.getCurrentPosition().toInt()
                    }
                    handler.postDelayed(this, 250)
                }
            }

            // Observe lifecycle để tự động pause/release khi tắt màn hình hoặc Activity bị destroy
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        if (exoPlayerHelper.isPlaying()) {
                            exoPlayerHelper.pause()
                            binding.btnPlay.visibility = View.VISIBLE
                            handler.removeCallbacks(updateProgressRunnable)
                        }
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        dialog.dismiss()
                    }

                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

            exoPlayerHelper.initPlayer(binding.playerView)
            exoPlayerHelper.setMedia(Const.VIDEO_INSTRUCTION_URL.toUri())
            exoPlayerHelper.getPLayer()?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        val duration = exoPlayerHelper.getDuration()
                        if (exoPlayerHelper.isPlaying()) binding.btnPlay.visibility = View.GONE
                        if (duration != C.TIME_UNSET) {
                            binding.sbVideo.max = duration.toInt()
                            handler.removeCallbacks(updateProgressRunnable)
                            handler.post(updateProgressRunnable)
                        }
                    } else if (playbackState == Player.STATE_ENDED) {
                        binding.btnPlay.visibility = View.VISIBLE
                        binding.sbVideo.progress = binding.sbVideo.max
                        handler.removeCallbacks(updateProgressRunnable)
                    }
                }
            })

            with(binding) {
                cvPlayer.setOnClickListener {
                    if (exoPlayerHelper.isPlaying()) {
                        btnPlay.visibility = View.VISIBLE
                        exoPlayerHelper.pause()
                    } else {
                        if (exoPlayerHelper.getPLayer()?.playbackState == Player.STATE_ENDED || sbVideo.progress == sbVideo.max) {
                            exoPlayerHelper.seekTo(0)
                        }
                        btnPlay.visibility = View.GONE
                        exoPlayerHelper.play()
                        handler.post(updateProgressRunnable)
                    }
                }

                sbVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean,
                    ) {
                        // Trống: không gọi seekTo liên tục khi đang kéo để tránh giật lag video
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // Tạm dừng vòng lặp cập nhật UI để tránh xung đột thao tác kéo
                        handler.removeCallbacks(updateProgressRunnable)
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // Chỉ gọi seekTo 1 lần duy nhất khi người dùng thả ngón tay ra
                        if (seekBar != null) {
                            exoPlayerHelper.seekTo(seekBar.progress.toLong())
                        }
                        // Chạy lại vòng lặp cập nhật UI
                        handler.post(updateProgressRunnable)
                    }
                })

                btnOk.setOnClickListener {
                    dialog.dismiss()
                }
            }

            dialog.setOnDismissListener {
                handler.removeCallbacks(updateProgressRunnable)
                exoPlayerHelper.release()
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            }

            dialog.show()
            applyImmersiveMode(dialog)
        }
    }
}