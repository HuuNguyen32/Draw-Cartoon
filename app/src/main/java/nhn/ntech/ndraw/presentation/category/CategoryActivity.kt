package nhn.ntech.ndraw.presentation.category

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.net.toUri
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.data.implemention.ItemRepositoryImpl
import nhn.ntech.ndraw.data.local.AppDatabase
import nhn.ntech.ndraw.databinding.ActivityCategoryBinding
import nhn.ntech.ndraw.domain.state.UiState
import nhn.ntech.ndraw.helper.NetworkObserver
import nhn.ntech.ndraw.presentation.home.MainAdapter
import nhn.ntech.ndraw.presentation.home.SpacingItemDecoration
import nhn.ntech.ndraw.presentation.sketching.SketchingActivity
import nhn.ntech.ndraw.utils.TransferUtils
import nhn.ntech.ndraw.helper.PermissionManager
import nhn.ntech.ndraw.utils.DialogUtils

class CategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemAdapter: MainAdapter
    private var pendingItemUri: Uri? = null
    private val networkObserver by lazy { NetworkObserver.getNetworkObserver(this) }
    private var wasNetworkLost: Boolean = false

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val targetUri = pendingItemUri
                pendingItemUri = null
                if (targetUri != null) {
                    handleImageUri(targetUri)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        setViewModel()
        setAdapters()
        setOnClickListener()
        observeState()
        observeNetwork()
        viewModel.loadData()
    }

    private fun observeNetwork() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkObserver.isOnline.collect { isOnline ->
                    if (isOnline) {
                        if (wasNetworkLost) {
                            wasNetworkLost = false
                            Toast.makeText(
                                this@CategoryActivity,
                                getString(R.string.internet_connected_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (viewModel.uiState.value is UiState.Error) {
                            viewModel.loadData()
                        }
                    } else {
                        if (networkObserver.shouldShowOfflineDialog()) {
                            wasNetworkLost = true
                            DialogUtils.createConfirmDialog(
                                this@CategoryActivity,
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
        val factory = CategoryViewModelFactory(itemRepository)
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.shimmerContainer.visibility = View.VISIBLE
                            binding.shimmerContainer.startShimmer()
                            binding.ivNoInternet.visibility = View.GONE
                            binding.listRecyclerView.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            binding.shimmerContainer.stopShimmer()
                            binding.shimmerContainer.visibility = View.GONE
                            binding.listRecyclerView.visibility = View.VISIBLE
                            binding.ivNoInternet.visibility = View.GONE
                            val data = state.data
                            categoryAdapter.updateData(data.categories.map { it.category })
                            val selectedIndex = data.categories.indexOfFirst {
                                it.category == data.selectedCategory
                            }
                            if (selectedIndex >= 0) {
                                categoryAdapter.setSelectedPosition(selectedIndex)
                            }
                            itemAdapter.updateData(data.items)
                        }

                        is UiState.Error -> {
                            binding.shimmerContainer.stopShimmer()
                            binding.shimmerContainer.visibility = View.GONE
                            binding.listRecyclerView.visibility = View.GONE
                            binding.ivNoInternet.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun setAdapters() {
        itemAdapter = MainAdapter(items = emptyList()) { item, isError ->
            if (isError) DialogUtils.createConfirmDialog(
                this@CategoryActivity,
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

        categoryAdapter = CategoryAdapter(categories = emptyList()) { category ->
            viewModel.selectCategory(category)
        }

        with(binding) {
            categoryRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(
                        this@CategoryActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                addItemDecoration(
                    SpacingItemDecoration(
                        TransferUtils.dpToPx(
                            this@CategoryActivity,
                            16
                        )
                    )
                )
                adapter = categoryAdapter
            }

            listRecyclerView.apply {
                layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                        gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    }
                adapter = itemAdapter
                setHasFixedSize(true)
            }
        }
    }

    private fun handleImageUri(uri: Uri) {
        val intent = Intent(this, SketchingActivity::class.java)
        intent.putExtra(Const.IMAGE_URI_TAG, uri.toString())
        startActivity(intent)
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
            }
        )
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}