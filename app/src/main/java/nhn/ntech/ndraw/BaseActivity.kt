package nhn.ntech.ndraw

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.hideSystemUI
import nhn.ntech.ndraw.utils.LanguageUtils

abstract class BaseActivity<VB: ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding ?: throw IllegalStateException("Should only use binding after onCreateView and before onDestroyView")

    protected abstract fun inflateBinding(layoutInflater: LayoutInflater): VB

    override fun attachBaseContext(newBase: Context) {
        val lang = UserPreferences(newBase).getLanguage() ?: "en"
        super.attachBaseContext(LanguageUtils.wrapContext(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageUtils.setLocale(this, UserPreferences(this).getLanguage() ?: "en")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        hideSystemUI(true)
        enableEdgeToEdge()
        _binding = inflateBinding(layoutInflater)
        initView()
        initData()
        initListener()
        observeData()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(true)
    }

    protected open fun initView() {}
    protected open fun initData() {}
    protected open fun initListener() {}
    protected open fun observeData() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}