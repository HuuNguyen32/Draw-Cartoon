package nhn.ntech.ndraw.presentation.category

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivityCategoryBinding
import nhn.ntech.ndraw.presentation.home.MainAdapter
import nhn.ntech.ndraw.presentation.home.SpacingItemDecoration
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
        val categories = listOf(
            "Adventure Time",
            "Tom & Jerry",
            "Oggy & the Cockroaches",
            "Anime",
            "Comic",
            "Manga",
            "Cartoon"
        )

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

        categoryAdapter = CategoryAdapter(categories = categories) { category ->

        }
        categoryAdapter.setSelectedPosition(0)

        itemAdapter = MainAdapter(items = items) { item ->

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

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}