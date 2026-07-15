package nhn.ntech.ndraw.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.presentation.language.Language
import java.util.Locale

object LanguageUtils {
    val listLanguage = listOf<Language>(
        Language("en", "English", R.drawable.flag_english),
        Language("es", "Spanish", R.drawable.flag_spanish),
        Language("fr", "French", R.drawable.flag_france),
        Language("hi", "Hindi", R.drawable.flag_hindi),
        Language("pt", "Portuguese", R.drawable.flag_portugeese),
        Language("de", "German", R.drawable.flag_german),
        Language("id", "Indonesian", R.drawable.flag_indonesian)
    )

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun applyLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}