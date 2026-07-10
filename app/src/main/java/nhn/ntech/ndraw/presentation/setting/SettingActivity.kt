package nhn.ntech.ndraw.presentation.setting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var adapter: SettingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        setAdapter()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        val items = listOf(
            SettingItem(1, R.drawable.ic_language, "Language"),
            SettingItem(2, R.drawable.ic_rate, "Rate Us"),
            SettingItem(3, R.drawable.ic_share, "Share App"),
            SettingItem(4, R.drawable.ic_sheild, "Privacy Policy")
        )
        adapter = SettingAdapter(items) {
            when (it.id) {
                1 -> {}
                2 -> {}
                3 -> {}
                4 -> {}
            }
        }
        binding.settingRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.settingRecyclerView.adapter = adapter
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}