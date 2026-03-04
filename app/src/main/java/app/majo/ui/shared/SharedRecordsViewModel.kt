package app.majo.ui.shared

import androidx.lifecycle.ViewModel
import app.majo.ui.util.atStartOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedRecordsViewModel : ViewModel() {
    private val _currentDayStartMs = MutableStateFlow(System.currentTimeMillis().atStartOfDay())
    val currentDayStartMs: StateFlow<Long> = _currentDayStartMs.asStateFlow()

    fun updateCurrentDayStartMs(ms: Long) {
        _currentDayStartMs.value = ms
    }
}