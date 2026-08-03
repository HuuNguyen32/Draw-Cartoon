package nhn.ntech.ndraw.presentation.permission

import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.navigateTo
import nhn.ntech.ndraw.ext.setTextColor
import nhn.ntech.ndraw.ext.setTextGradientColor

import nhn.ntech.ndraw.helper.PermissionManager

class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    private lateinit var viewModel: PermissionViewModel
    private val userPreferences by lazy { UserPreferences(this) }

    companion object {
        private const val REQUEST_MEDIA_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 200
    }

    private val photoPermission = PermissionManager.photoPermission
    private val cameraPermission = PermissionManager.cameraPermission

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setPaddingScreen()
        viewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        initViews()
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
            PermissionManager.checkMediaPermission(
                activity = this,
                onGranted = {
                    binding.switchPermission.isChecked = true
                    viewModel.setPermissionGranted(true)
                },
                onLaunchLauncher = {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(photoPermission),
                        REQUEST_MEDIA_PERMISSION
                    )
                }
            )
        }

        binding.cameraPermissionSwitchContainer.setOnClickListener {
            PermissionManager.checkCameraPermission(
                activity = this,
                onGranted = {
                    binding.cameraSwitchPermission.isChecked = true
                    viewModel.setCameraPermissionGranted(true)
                },
                onLaunchLauncher = {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(cameraPermission),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
            )
        }

        binding.btnContinue.setOnClickListener {
            userPreferences.isPermissionScreenVisited(true)
            navigateTo<MainActivity>(finishAffinity = true)
        }
    }

    private fun initViews() {
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

        }

        viewModel.isCameraPermissionGranted.observe(this) { isCameraPermissionGranted ->

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
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                binding.switchPermission.isChecked = true
                viewModel.setPermissionGranted(true)
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                binding.switchPermission.isChecked = false
                viewModel.setPermissionGranted(false)
            }
        }

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                binding.cameraSwitchPermission.isChecked = true
                viewModel.setCameraPermissionGranted(true)
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                binding.cameraSwitchPermission.isChecked = false
                viewModel.setCameraPermissionGranted(false)
            }
        }
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}