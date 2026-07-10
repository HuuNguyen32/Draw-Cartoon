package nhn.ntech.ndraw.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivityMainBinding
import nhn.ntech.ndraw.presentation.setting.SettingActivity
import nhn.ntech.ndraw.utils.TransferUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel

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
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.trendingRecyclerView.adapter = adapter
        binding.trendingRecyclerView.addItemDecoration(
            SpacingItemDecoration(
                TransferUtils.dpToPx(
                    this,
                    6
                ), 2
            )
        )
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}