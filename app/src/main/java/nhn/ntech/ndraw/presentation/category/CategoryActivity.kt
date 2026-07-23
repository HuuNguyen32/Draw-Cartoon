package nhn.ntech.ndraw.presentation.category

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityCategoryBinding
import nhn.ntech.ndraw.presentation.home.MainAdapter
import nhn.ntech.ndraw.presentation.home.SpacingItemDecoration
import nhn.ntech.ndraw.presentation.sketching.SketchingActivity
import nhn.ntech.ndraw.utils.TransferUtils

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import nhn.ntech.ndraw.helper.PermissionManager

class CategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemAdapter: MainAdapter
    private var pendingItemUri: Uri? = null

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
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        setAdapters()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun setAdapters() {
        itemAdapter = MainAdapter(items = emptyList()) { item ->
            checkCameraPermission(item)
        }

        categoryAdapter = CategoryAdapter(categories = emptyList()) { category ->
            loadCategoryItems(category)
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

        lifecycleScope.launch(Dispatchers.IO) {
            val categories = assets.list(Const.FOLDER_ASSETS)?.toList() ?: emptyList()
            val firstCategory = categories.getOrNull(0) ?: ""
            val items = if (firstCategory.isNotEmpty()) {
                assets.list(Const.getAssetsPath(firstCategory))
                    ?.sorted()
                    ?.map { "${Const.getAssetsPath(firstCategory)}/$it" }
                    ?: emptyList()
            } else emptyList()

            withContext(Dispatchers.Main) {
                categoryAdapter.updateData(categories)
                categoryAdapter.setSelectedPosition(0)
                itemAdapter.updateData(items)
            }
        }
    }

    private fun loadCategoryItems(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imgList = assets.list(Const.getAssetsPath(category))
                ?.sorted()
                ?.map { "${Const.getAssetsPath(category)}/$it" }
                ?: emptyList()
            withContext(Dispatchers.Main) {
                itemAdapter.updateData(imgList)
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