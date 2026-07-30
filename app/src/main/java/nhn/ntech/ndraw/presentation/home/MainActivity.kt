package nhn.ntech.ndraw.presentation.home

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.data.implemention.ItemRepositoryImpl
import nhn.ntech.ndraw.data.local.AppDatabase
import nhn.ntech.ndraw.databinding.ActivityMainBinding
import nhn.ntech.ndraw.domain.state.UiState
import nhn.ntech.ndraw.presentation.category.CategoryActivity
import nhn.ntech.ndraw.presentation.setting.SettingActivity
import nhn.ntech.ndraw.presentation.sketching.SketchingActivity
import nhn.ntech.ndraw.presentation.work.MyWorkActivity
import nhn.ntech.ndraw.utils.DialogUtils
import nhn.ntech.ndraw.helper.PermissionManager

import android.view.View
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.reviewApp
import nhn.ntech.ndraw.helper.NetworkObserver
import nhn.ntech.ndraw.utils.LanguageUtils
import nhn.ntech.ndraw.utils.ToastUtils

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userPreferences by lazy { UserPreferences(this) }
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel
    private var photoUri: Uri? = null
    private var pendingItemUri: Uri? = null
    private val networkObserver by lazy { NetworkObserver.getNetworkObserver(this) }
    private var wasNetworkLost: Boolean = false
    private var hasLostInternet: Boolean = false

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val targetUri = pendingItemUri
                pendingItemUri = null
                if (targetUri != null) {
                    handleImageUri(targetUri)
                } else {
                    openCamera()
                }
            } else {
                pendingItemUri = null
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val mediaPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.media_permission_error_message),
                    Toast.LENGTH_SHORT
                ).show()
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
        setViewModel()
        val currentTotalUseApp = userPreferences.getTotalUseApp()
        userPreferences.setTotalUseApp(currentTotalUseApp + 1)
        setAdapter()
        setOnClickListener()
        observeState()
        observeNetwork()
        viewModel.fetchData()
    }

    private fun observeNetwork() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkObserver.isOnline.collect { isOnline ->
                    if (isOnline) {
                        hasLostInternet = false
                        if (wasNetworkLost) {
                            wasNetworkLost = false
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.internet_connected_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (viewModel.uiState.value is UiState.Error) {
                            viewModel.fetchData()
                        }
                    } else {
                        hasLostInternet = true
                        if (networkObserver.shouldShowOfflineDialog()) {
                            wasNetworkLost = true
                            DialogUtils.createConfirmDialog(
                                this@MainActivity,
                                getString(R.string.no_internet_title),
                                getString(R.string.no_internet_connected_message),
                                getString(R.string.open_settings_title),
                                getString(R.string.close_title),
                                onConfirm = {
                                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                    startActivity(intent)
                                })
                        }
                    }
                }
            }
        }
    }

    private fun setViewModel() {
        val db = AppDatabase.getDatabase(this)
        val itemRepository = ItemRepositoryImpl(db)
        val factory = MainViewModelFactory(itemRepository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.shimmerContainer?.visibility = View.VISIBLE
                            binding.shimmerContainer?.startShimmer()
                            binding.trendingRecyclerView.visibility = View.GONE
                            binding.ivNoInternet?.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            binding.shimmerContainer?.stopShimmer()
                            binding.shimmerContainer?.visibility = View.GONE
                            binding.trendingRecyclerView.visibility = View.VISIBLE
                            binding.ivNoInternet?.visibility = View.GONE
                            render(state.data)
                        }

                        is UiState.Error -> {
                            binding.shimmerContainer?.stopShimmer()
                            binding.shimmerContainer?.visibility = View.GONE
                            binding.trendingRecyclerView.visibility = View.GONE
                            binding.ivNoInternet?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun render(state: MainUIState) = with(binding) {
        adapter.updateData(state.trendingList)
    }

    private fun setOnClickListener() {
        with(binding) {
            btnI.setOnClickListener {
                DialogUtils.createInstructionDialog(this@MainActivity, this@MainActivity)
            }

            btnSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }

            btnCreate.setOnClickListener {
                DialogUtils.createDrawDialog(
                    this@MainActivity,
                    fromCamera = {
                        pendingItemUri = null
                        checkCameraPermissionAndOpen()
                    },
                    fromGallery = {
                        checkCameraPermissionAndOpenGallery()
                    }
                )
            }

            btnCategory.setOnClickListener {
                startActivity(Intent(this@MainActivity, CategoryActivity::class.java))
            }

            btnMyWork.setOnClickListener {
                startActivity(Intent(this@MainActivity, MyWorkActivity::class.java))
            }
        }
    }

    private fun setAdapter() {
        adapter = MainAdapter(emptyList()) { item ->
            if (hasLostInternet) DialogUtils.createConfirmDialog(
                this@MainActivity,
                getString(R.string.unable_load_image_title),
                getString(R.string.unable_load_image_message),
                getString(R.string.open_settings_title),
                getString(R.string.close_title),
                onConfirm = {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                })
            else checkCameraPermission(item.toUri())
        }
        binding.trendingRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
        binding.trendingRecyclerView.adapter = adapter
        binding.trendingRecyclerView.setHasFixedSize(true)
    }

    private fun checkCameraPermissionAndOpen() {
        PermissionManager.checkCameraPermission(
            activity = this,
            onGranted = { openCamera() },
            onLaunchLauncher = { cameraPermissionLauncher.launch(PermissionManager.cameraPermission) }
        )
    }

    private fun checkMediaPermissionAndOpen() {
        PermissionManager.checkMediaPermission(
            activity = this,
            onGranted = { openGallery() },
            onLaunchLauncher = { mediaPermissionLauncher.launch(PermissionManager.photoPermission) }
        )
    }

    private fun checkCameraPermissionAndOpenGallery() {
        PermissionManager.checkCameraPermission(
            activity = this,
            onGranted = { openGallery() },
            onLaunchLauncher = { cameraPermissionLauncher.launch(PermissionManager.cameraPermission) },
            description = getString(R.string.camera_permission_settings_des)
        )
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "ndraw_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        photoUri?.let { cameraLauncher.launch(it) }
            ?: Toast.makeText(
                this,
                getString(R.string.create_image_fail_message),
                Toast.LENGTH_SHORT
            ).show()
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

    private fun checkCameraPermission(item: Uri) {
        pendingItemUri = item
        PermissionManager.checkCameraPermission(
            activity = this,
            onGranted = {
                pendingItemUri = null
                handleImageUri(item)
            },
            onLaunchLauncher = {
                cameraPermissionLauncher.launch(PermissionManager.cameraPermission)
            },
            description = getString(R.string.camera_permission_settings_des)
        )
    }

    override fun onRestart() {
        super.onRestart()
        LanguageUtils.setLocale(this, UserPreferences(this).getLanguage() ?: "en")
        with(binding) {
            tvTitle.text = getString(R.string.draw_cartoon_title)
            tvCreate?.text = getString(R.string.create_tv)
            tvCategory?.text = getString(R.string.category_tv)
            tvMyWork?.text = getString(R.string.my_work_tv)
            tvTrending.text = getString(R.string.trending_title)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (!userPreferences.isRateApp() && userPreferences.getTotalUseApp() % 2 == 0) {
            DialogUtils.createRateDialog(this@MainActivity) { rate ->
                when (rate) {
                    0 -> ToastUtils.showToast(
                        context = this@MainActivity,
                        message = getString(R.string.please_select_stars_des),
                    )

                    in 1..3 -> {
                        userPreferences.setRateApp(true)
                        super.onBackPressed()
                    }

                    else -> {
                        userPreferences.setRateApp(true)
                        reviewApp(this, false)
                    }
                }
            }
        } else {
            super.onBackPressed()
        }
    }
}