package nhn.ntech.ndraw.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivitySettingBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.navigateTo
import nhn.ntech.ndraw.presentation.language.LanguageActivity
import nhn.ntech.ndraw.utils.DialogUtils
import nhn.ntech.ndraw.ext.policy
import nhn.ntech.ndraw.ext.reviewApp
import nhn.ntech.ndraw.ext.shareApp
import nhn.ntech.ndraw.utils.ToastUtils

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    private lateinit var adapter: SettingAdapter
    private val userPreferences by lazy { UserPreferences(this) }
    private val items: List<SettingItem>
        get() = if (userPreferences.isRateApp()) {
            listOf(
                SettingItem(1, R.drawable.ic_language, getString(R.string.language_title)),
                SettingItem(3, R.drawable.ic_share, getString(R.string.share_app_title)),
                SettingItem(4, R.drawable.ic_sheild, getString(R.string.privacy_title))
            )
        } else {
            listOf(
                SettingItem(1, R.drawable.ic_language, getString(R.string.language_title)),
                SettingItem(2, R.drawable.ic_rate, getString(R.string.rate_title)),
                SettingItem(3, R.drawable.ic_share, getString(R.string.share_app_title)),
                SettingItem(4, R.drawable.ic_sheild, getString(R.string.privacy_title))
            )
        }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        adapter = SettingAdapter(items) {
            when (it.id) {
                1 -> {
                    navigateTo<LanguageActivity> {
                        putExtra(Const.FLOW_TAG, Const.FLOW_SETTING_CODE)
                    }
                }

                2 -> {
                    DialogUtils.createRateDialog(this) { rate ->
                        when (rate) {
                            0 -> ToastUtils.showToast(
                                context = this@SettingActivity,
                                message = getString(R.string.please_select_stars_des),
                            )

                            in 1..3 -> {
                                userPreferences.setRateApp(true)
                                adapter.setData(items)
                            }

                            else -> {
                                userPreferences.setRateApp(true)
                                adapter.setData(items)
                                reviewApp(this, false)
                            }
                        }
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