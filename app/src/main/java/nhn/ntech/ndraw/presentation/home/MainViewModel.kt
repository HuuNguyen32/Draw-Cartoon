package nhn.ntech.ndraw.presentation.home

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nhn.ntech.ndraw.consts.Const

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun loadData(assets: AssetManager) {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                val folder = Const.getAssetsPath("cute")
                assets.list(folder)
                    ?.sorted()
                    ?.map { "$folder/$it" }
                    ?: emptyList()
            }
            _uiState.update { state -> state.copy(trendingList = items) }
        }
    }
}