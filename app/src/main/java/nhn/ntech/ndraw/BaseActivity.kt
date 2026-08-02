package nhn.ntech.ndraw

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.ext.hideSystemUI
import nhn.ntech.ndraw.utils.LanguageUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = UserPreferences(newBase).getLanguage() ?: "en"
        super.attachBaseContext(LanguageUtils.wrapContext(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageUtils.setLocale(this, UserPreferences(this).getLanguage() ?: "en")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        hideSystemUI(true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(true)
    }
}