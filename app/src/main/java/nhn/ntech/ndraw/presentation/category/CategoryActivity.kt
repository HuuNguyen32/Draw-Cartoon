package nhn.ntech.ndraw.presentation.category

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityCategoryBinding
import nhn.ntech.ndraw.presentation.home.MainAdapter
import nhn.ntech.ndraw.presentation.home.SpacingItemDecoration
import nhn.ntech.ndraw.presentation.sketching.SketchingActivity
import nhn.ntech.ndraw.utils.TransferUtils

class CategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemAdapter: MainAdapter

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
        val categories = assets.list(Const.FOLDER_ASSETS)?.toList() ?: emptyList()

        val items = assets.list(Const.getAssetsPath(categories[0]))
            ?.sorted()
            ?.map { "${Const.getAssetsPath(categories[0])}/$it" }
            ?: emptyList()

        categoryAdapter = CategoryAdapter(categories = categories) { category ->
            val imgList = assets.list(Const.getAssetsPath(category))
                ?.sorted()
                ?.map { "${Const.getAssetsPath(category)}/$it" }
                ?: emptyList()
            itemAdapter.updateData(imgList)
        }
        categoryAdapter.setSelectedPosition(0)

        itemAdapter = MainAdapter(items = items) { item ->
            handleImageUri(item)
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
                        gapStrategy =
                            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                    }
                adapter = itemAdapter
            }

        }
    }

    private fun handleImageUri(uri: Uri) {
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