package nhn.ntech.ndraw.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.DoubleButtonDialogBinding
import nhn.ntech.ndraw.databinding.RateDialogBinding

class DialogUtils {
    companion object {
        fun createRateDialog(context: Context, onRate: () -> Unit) {
            val dialog = Dialog(context)
            val binding = RateDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rate_dialog_bg)
            with(binding) {
                ivRate.setImageResource(R.drawable.img_rate_0)
                tvTitle.text = context.getString(R.string.no_star_title)
                tvDes.text = context.getString(R.string.no_star_des)
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
                    onRate()
                }

                btnExit.setOnClickListener {
                    dialog.dismiss()
                }
            }
            dialog.show()
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
        }
    }
}