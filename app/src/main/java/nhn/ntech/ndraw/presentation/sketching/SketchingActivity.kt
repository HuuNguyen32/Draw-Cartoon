package nhn.ntech.ndraw.presentation.sketching

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivitySketchingBinding
import nhn.ntech.ndraw.utils.setTextGradientColor
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SketchingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySketchingBinding
    private lateinit var viewModel: SketchingViewModel
    private lateinit var photoUri: Uri

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
            btnGuide.setOnClickListener {
                Toast.makeText(this@SketchingActivity, "Show video guide!", Toast.LENGTH_SHORT)
                    .show()
            }
            btnLock.setOnClickListener { viewModel.toggleLock() }

            btnUnlock.setOnClickListener { viewModel.toggleLock() }

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
        }
    }

    private fun initView() {
        with(binding) {
            val colors = intArrayOf(
                "#B7ADF4".toColorInt(),
                "#DFA1F6".toColorInt()
            )
            val positions = floatArrayOf(
                0f,
                1f
            )
            tvPhoto.setTextGradientColor(colors = colors, positions = positions)

            ivSticker.setImageURI(photoUri)
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