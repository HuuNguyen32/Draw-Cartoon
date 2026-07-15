package nhn.ntech.ndraw.presentation.home

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityMainBinding
import nhn.ntech.ndraw.presentation.category.CategoryActivity
import nhn.ntech.ndraw.presentation.setting.SettingActivity
import nhn.ntech.ndraw.presentation.sketching.SketchingActivity
import nhn.ntech.ndraw.utils.DialogUtils

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel
    private var photoUri: Uri? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri -> handleImageUri(uri) }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleImageUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        setAdapter()
        setOnClickListener()

    }

    private fun setOnClickListener() {
        with(binding) {
            btnSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }

            btnCreate.setOnClickListener {
                DialogUtils.createDrawDialog(
                    this@MainActivity,
                    fromCamera = {
                        checkCameraPermissionAndOpen()
                    },
                    fromGallery = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            btnCategory.setOnClickListener {
                startActivity(Intent(this@MainActivity, CategoryActivity::class.java))
            }
        }
    }

    private fun setAdapter() {
        val items = listOf(
            R.drawable.trend_test_1,
            R.drawable.trend_test_2,
            R.drawable.trend_test,
            R.drawable.trend_test,
            R.drawable.trend_test,
            R.drawable.trend_test_1,
            R.drawable.trend_test_2,
            R.drawable.trend_test,
            R.drawable.trend_test,
            R.drawable.trend_test,
            R.drawable.trend_test_1,
            R.drawable.trend_test_2,
            R.drawable.trend_test,
            R.drawable.trend_test_1,
            R.drawable.trend_test_2,
            R.drawable.trend_test
        )
        adapter = MainAdapter(items) {

        }
        binding.trendingRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
        binding.trendingRecyclerView.adapter = adapter
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "ndraw_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        photoUri?.let { cameraLauncher.launch(it) }
            ?: Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show()
    }

    private fun handleImageUri(uri: Uri) {
        Log.d("MainActivity", "handleImageUri: $uri")
        val intent = Intent(this, SketchingActivity::class.java)
        intent.putExtra(Const.IMAGE_URI_TAG, uri.toString())
        startActivity(intent)
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}