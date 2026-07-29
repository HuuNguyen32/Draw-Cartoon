package nhn.ntech.ndraw.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivitySplashBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.presentation.Intro.IntroActivity
import nhn.ntech.ndraw.presentation.language.LanguageActivity
import nhn.ntech.ndraw.utils.LanguageUtils

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        userPreferences = UserPreferences(this)
        val factory = SplashViewModelFactory(userPreferences)
        viewModel = ViewModelProvider(this, factory = factory)[SplashViewModel::class.java]

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        lifecycleScope.launch {
            viewModel.isLanguageSet.collect { language ->
                if (language == null) return@collect

                delay(3000)
                if (language.isEmpty()) {
                    val intent = Intent(this@SplashActivity, LanguageActivity::class.java)
                    intent.putExtra(Const.FLOW_TAG, Const.FLOW_SPLASH_CODE)
                    startActivity(intent)
                    finish()
                } else {
                    LanguageUtils.setLocale(this@SplashActivity, language)
                    val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.loadData()
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}