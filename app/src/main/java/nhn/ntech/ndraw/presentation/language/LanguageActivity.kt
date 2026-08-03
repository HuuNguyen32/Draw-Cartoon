package nhn.ntech.ndraw.presentation.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityLanguageBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.navigateTo
import nhn.ntech.ndraw.presentation.Intro.IntroActivity
import nhn.ntech.ndraw.presentation.home.MainActivity

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    private lateinit var adapter: LanguageAdapter
    private lateinit var viewModel: LanguageViewModel
    private lateinit var userPreferences: UserPreferences
    private lateinit var languageSelected: Language
    private var flow: Int = -1

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = LanguageViewModelFactory(userPreferences)
        viewModel =
            ViewModelProvider(owner = this, factory = factory)[LanguageViewModel::class.java]
        flow = intent.getIntExtra(Const.FLOW_TAG, -1)
        setPaddingScreen()
        initViews()
        setAdapter()
        observerState()
        setOnClickListener()
    }

    private fun initViews() {
        if (flow == Const.FLOW_SETTING_CODE) {
            binding.btnBack.visibility = View.VISIBLE
            binding.tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
        } else {
            binding.btnBack.visibility = View.GONE
            binding.tvTitle.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
    }

    private fun observerState() {
        viewModel.language.observe(this) { language ->
            languageSelected = language
            Log.d("Language", language.name)
        }
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCheck.setOnClickListener {
            viewModel.saveLanguage(languageSelected)
            when (flow) {
                Const.FLOW_SPLASH_CODE -> {
                    navigateTo<IntroActivity>(finishCurrent = true)
                }

                Const.FLOW_SETTING_CODE -> {
                    navigateTo<MainActivity>() {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }

                else -> finish()
            }
        }
    }

    private fun setAdapter() {
        adapter =
            LanguageAdapter(viewModel.languageList.value ?: emptyList()) { language, isSelected ->
                viewModel.setLanguage(language)
                if (isSelected) binding.btnCheck.visibility = View.VISIBLE
            }
        binding.rvLanguage.adapter = adapter
        binding.rvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.loadLanguageList()
        viewModel.languageList.observe(this) { languageList ->
            adapter.updateData(languageList)
            if (languageList.isNotEmpty()) {
                val position = getLanguagePosition(languageList)
                if (position != -1) {
                    binding.btnCheck.visibility = View.VISIBLE
                    adapter.setSelectedPosition(position)
                    viewModel.setLanguage(languageList[position])
                }
            }
        }
    }

    private fun getLanguagePosition(languageList: List<Language>): Int {
        val currentLanguage = viewModel.getLanguage() ?: return -1
        for (i in languageList.indices) {
            if (languageList[i].code == currentLanguage) {
                return i
            }
        }
        return -1
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}