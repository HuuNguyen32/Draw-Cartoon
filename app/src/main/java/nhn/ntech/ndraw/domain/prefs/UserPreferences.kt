package nhn.ntech.ndraw.domain.prefs

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveLanguage(language: String) {
        editor.putString("language", language)
        editor.apply()
    }

    fun getLanguage(): String? {
        return sharedPreferences.getString("language", null)
    }

    fun isPermissionScreenVisited(isVisited: Boolean) {
        editor.putBoolean("permission_screen_visited", isVisited)
        editor.apply()
    }

    fun getPermissionScreenVisited(): Boolean {
        return sharedPreferences.getBoolean("permission_screen_visited", false)
    }
}