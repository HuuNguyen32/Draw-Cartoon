package nhn.ntech.ndraw.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.observeWithLifecycle(
    owner: LifecycleOwner,
    minState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend (T) -> Unit,
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minState) {
            collect {
                block(it)
            }
        }
    }
}

inline fun <reified T : Activity> Context.navigateTo(
    finishCurrent: Boolean = false,
    finishAffinity: Boolean = false,
    extras: Intent.() -> Unit = {},
) {
    val intent = Intent(this, T::class.java).apply(extras)
    startActivity(intent)
    if (finishCurrent && this is Activity) finish()
    if (finishAffinity && this is Activity) finishAffinity()
}


