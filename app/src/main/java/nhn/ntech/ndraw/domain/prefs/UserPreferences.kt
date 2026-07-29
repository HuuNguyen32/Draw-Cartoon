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

    fun setCameraAskedBefore(asked: Boolean) {
        editor.putBoolean("camera_asked_before", asked).apply()
    }

    fun isCameraAskedBefore(): Boolean {
        return sharedPreferences.getBoolean("camera_asked_before", false)
    }

    fun setMediaAskedBefore(asked: Boolean) {
        editor.putBoolean("media_asked_before", asked).apply()
    }

    fun isMediaAskedBefore(): Boolean {
        return sharedPreferences.getBoolean("media_asked_before", false)
    }

    fun setTotalUseApp(total: Int) {
        editor.putInt("total_use_app", total).apply()
    }

    fun getTotalUseApp(): Int {
        return sharedPreferences.getInt("total_use_app", 0)
    }

    fun setRateApp(isRate: Boolean) {
        editor.putBoolean("rate_app", isRate).apply()
    }

    fun isRateApp(): Boolean {
        return sharedPreferences.getBoolean("rate_app", false)
    }
}