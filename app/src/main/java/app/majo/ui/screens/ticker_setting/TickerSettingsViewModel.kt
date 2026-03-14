package app.majo.ui.screens.ticker_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.majo.domain.model.action.Action
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TickerSettingsViewModel(
    private val actionRepository: ActionRepository
) : ViewModel() {
    private val _actions = MutableStateFlow<List<Action>>(emptyList())
    val actions: StateFlow<List<Action>> = _actions.asStateFlow()

    private val _tickerMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val tickerMap: StateFlow<Map<Long, String>> = _tickerMap.asStateFlow()

    init {
        viewModelScope.launch {
            actionRepository.getActions().collect { list ->
                _actions.value = list
                _tickerMap.value = list.associate { it.id to it.ticker }
            }
        }
    }

    fun updateTicker(actionId: Long, ticker: String) {
        _tickerMap.update { current ->
            current.toMutableMap().apply { put(actionId, ticker) }
        }
    }

    fun saveTickers(onComplete: () -> Unit) {
        viewModelScope.launch {
            _tickerMap.value.forEach { (id, ticker) ->
                val action = _actions.value.find { it.id == id }
                if (action != null && action.ticker != ticker) {
                    val updated = action.copy(ticker = ticker)
                    actionRepository.update(updated)
                }
            }
            onComplete()
        }
    }
}