package nhn.ntech.ndraw.presentation.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivitySettingBinding
import nhn.ntech.ndraw.presentation.language.LanguageActivity
import nhn.ntech.ndraw.utils.DialogUtils
import nhn.ntech.ndraw.utils.policy
import nhn.ntech.ndraw.utils.reviewApp
import nhn.ntech.ndraw.utils.shareApp

class SettingActivity : BaseActivity() {

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
            SettingItem(1, R.drawable.ic_language, getString(R.string.language_title)),
            SettingItem(2, R.drawable.ic_rate, getString(R.string.rate_title)),
            SettingItem(3, R.drawable.ic_share, getString(R.string.share_app_title)),
            SettingItem(4, R.drawable.ic_sheild, getString(R.string.privacy_title))
        )
        adapter = SettingAdapter(items) {
            when (it.id) {
                1 -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra(Const.FLOW_TAG, Const.FLOW_SETTING_CODE)
                    startActivity(intent)
                }

                2 -> {
                    DialogUtils.createRateDialog(this) {
                        reviewApp(this, false)
                    }
                }

                3 -> {
                    shareApp()
                }

                4 -> {
                    policy()
                }
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