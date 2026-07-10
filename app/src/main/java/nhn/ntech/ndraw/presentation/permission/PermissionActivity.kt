package nhn.ntech.ndraw.presentation.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import nhn.ntech.ndraw.presentation.home.MainActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivityPermissionBinding
import nhn.ntech.ndraw.utils.setTextColor
import nhn.ntech.ndraw.utils.setTextGradientColor

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private lateinit var viewModel: PermissionViewModel

    companion object {
        private const val REQUEST_MEDIA_PERMISSION = 100
    }

    private var denyCount = 0
    private var isHasMediaPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        viewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        initView()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val isPermissionGranted =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        binding.switchPermission.isChecked = isPermissionGranted
        viewModel.setPermissionGranted(isPermissionGranted)
    }

    private fun setUpListeners() {
        binding.permissionSwitchContainer.setOnClickListener {
            requestMediaPermission()
        }

        binding.btnContinue.setOnClickListener {
            if (isHasMediaPermission) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, "Please enable permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView() {
        with(binding) {
            val colors = intArrayOf(
                "#B7ADF4".toColorInt(),
                "#DFA1F6".toColorInt()
            )
            val positions = floatArrayOf(0f, 1f)
            btnContinue.setTextGradientColor(colors = colors, positions = positions)

            tvPermissionDes.setTextColor(
                this@PermissionActivity,
                getString(R.string.permission_des),
                "Draw Cartoon: AR Drawing",
                R.color.dot_selected,
                Typeface.BOLD,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        viewModel.isPermissionGranted.observe(this) { isPermissionGranted ->
            isHasMediaPermission = isPermissionGranted
        }
    }

    private fun requestMediaPermission() {
        val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            photoPermission
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, photoPermission)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(photoPermission),
                    REQUEST_MEDIA_PERMISSION
                )
            } else {
                if (denyCount == 2) {
                    Toast.makeText(
                        this,
                        "Please enable permissions in Settings",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToSetting()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(photoPermission),
                        REQUEST_MEDIA_PERMISSION
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                binding.switchPermission.isChecked = true
                viewModel.setPermissionGranted(true)
            } else {
                denyCount++
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                binding.switchPermission.isChecked = false
                viewModel.setPermissionGranted(false)
                if (denyCount == 2) goToSetting()
            }
        }
    }

    private fun goToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}