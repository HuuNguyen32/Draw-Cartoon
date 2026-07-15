package nhn.ntech.ndraw

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.utils.LanguageUtils
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = UserPreferences(newBase).getLanguage() ?: "en"
        super.attachBaseContext(LanguageUtils.wrapContext(newBase, lang))
    }

    override fun onResume() {
        super.onResume()
        val savedLang = UserPreferences(this).getLanguage() ?: "en"
        val currentLang = resources.configuration.locales[0].language
        if (savedLang != currentLang) {
            recreate()
        }
    }
}