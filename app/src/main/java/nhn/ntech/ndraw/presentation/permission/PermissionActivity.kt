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
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.presentation.home.MainActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivityPermissionBinding
import nhn.ntech.ndraw.utils.setTextColor
import nhn.ntech.ndraw.utils.setTextGradientColor

class PermissionActivity : BaseActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private lateinit var viewModel: PermissionViewModel

    companion object {
        private const val REQUEST_MEDIA_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 200
    }

    private var denyCount = 0
    private var isHasMediaPermission = false
    private val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    private val cameraPermission = Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        viewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        initView()
        setUpListeners()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        val isPhotoPermissionGranted = requestCheckPermission(photoPermission)
        val isCameraPermissionGranted = requestCheckPermission(cameraPermission)
        binding.switchPermission.isChecked = isPhotoPermissionGranted
        viewModel.setPermissionGranted(isPhotoPermissionGranted)
        binding.cameraSwitchPermission.isChecked = isCameraPermissionGranted
        viewModel.setCameraPermissionGranted(isCameraPermissionGranted)
    }

    private fun setUpListeners() {
        binding.permissionSwitchContainer.setOnClickListener {
            requestMediaPermission(photoPermission, REQUEST_MEDIA_PERMISSION)
        }

        binding.cameraPermissionSwitchContainer.setOnClickListener {
            requestMediaPermission(cameraPermission, REQUEST_CAMERA_PERMISSION)
        }

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
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
                getString(R.string.permission_des_bold),
                R.color.dot_selected,
                Typeface.BOLD,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun observeState() {
        viewModel.isPermissionGranted.observe(this) { isPermissionGranted ->
            isHasMediaPermission = isPermissionGranted
        }

        viewModel.isCameraPermissionGranted.observe(this) { isCameraPermissionGranted ->

        }
    }

    private fun requestMediaPermission(permission: String, requestCode: Int) {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
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
                        arrayOf(permission),
                        requestCode
                    )
                }
            }
        }
    }

    private fun requestCheckPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                binding.cameraSwitchPermission.isChecked = true
                viewModel.setCameraPermissionGranted(true)
            } else {
                denyCount++
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                binding.cameraSwitchPermission.isChecked = false
                viewModel.setCameraPermissionGranted(false)
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