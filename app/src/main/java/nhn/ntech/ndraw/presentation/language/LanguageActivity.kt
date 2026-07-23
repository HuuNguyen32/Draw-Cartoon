package nhn.ntech.ndraw.presentation.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityLanguageBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.presentation.Intro.IntroActivity
import nhn.ntech.ndraw.presentation.home.MainActivity

class LanguageActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private lateinit var viewModel: LanguageViewModel
    private lateinit var userPreferences: UserPreferences
    private lateinit var languageSelected: Language
    private var flow: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = LanguageViewModelFactory(userPreferences)
        viewModel =
            ViewModelProvider(owner = this, factory = factory)[LanguageViewModel::class.java]
        flow = intent.getIntExtra(Const.FLOW_TAG, -1)
        setPaddingScreen()
        setAdapter()
        initData()
        setOnClickListener()
    }

    private fun initData() {
        viewModel.language.observe(this) { language ->
            languageSelected = language
            Log.d("Language", language.name)
        }
    }

    private fun setOnClickListener() {
        binding.btnCheck.setOnClickListener {
            viewModel.saveLanguage(languageSelected)
            when (flow) {
                Const.FLOW_SPLASH_CODE -> {
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }

                Const.FLOW_SETTING_CODE -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                else -> finish()
            }
        }
    }

    private fun setAdapter() {
        adapter = LanguageAdapter(viewModel.languageList.value ?: emptyList()) { language ->
            viewModel.setLanguage(language)
        }
        binding.rvLanguage.adapter = adapter
        binding.rvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.loadLanguageList()
        viewModel.languageList.observe(this) { languageList ->
            adapter.updateData(languageList)
            if (languageList.isNotEmpty()) {
                val position = getLanguagePosition(languageList)
                adapter.setSelectedPosition(position)
                viewModel.setLanguage(languageList[position])
            }
        }
    }

    private fun getLanguagePosition(languageList: List<Language>): Int {
        val currentLanguage = viewModel.getLanguage() ?: "en"
        for (i in languageList.indices) {
            if (languageList[i].code == currentLanguage) {
                return i
            }
        }
        return 0
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}