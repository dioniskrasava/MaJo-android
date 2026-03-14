package app.majo.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.majo.ui.screens.settings.SettingsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 1. Создаем DataStore. Используем делегат by preferencesDataStore.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "majo_settings")

// 2. Определяем ключи для наших настроек.
object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val ACCENT_COLOR = stringPreferencesKey("accent_color")
    val USE_ACTION_COLORS = booleanPreferencesKey("use_action_colors")
    val CARD_ALPHA = floatPreferencesKey("card_alpha")
    val USE_TICKERS_IN_MATRIX = booleanPreferencesKey("use_tickers_in_matrix")
}

class SettingsDataStore(private val context: Context) {

    // 3. Чтение настроек: возвращаем Flow<SettingsState>
    val settingsFlow = context.dataStore.data
        .map { preferences ->
            SettingsState(
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false,
                currentLanguageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: "ru",
                currentAccentColor = preferences[PreferencesKeys.ACCENT_COLOR] ?: "Purple",
                useActionColors = preferences[PreferencesKeys.USE_ACTION_COLORS] ?: true,   // по умолчанию true
                cardAlpha = preferences[PreferencesKeys.CARD_ALPHA] ?: 0.4f,
                useTickersInMatrix = preferences[PreferencesKeys.USE_TICKERS_IN_MATRIX] ?: false
            )
        }

    // 4. Запись темной темы
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.IS_DARK_MODE] = isDark
        }
    }

    // 5. Запись языка
    suspend fun setLanguageCode(code: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.LANGUAGE_CODE] = code
        }
    }

    // 6. Запись акцентного цвета
    suspend fun setAccentColor(color: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.ACCENT_COLOR] = color
        }
    }

    //
    suspend fun getLanguageCode(): String = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE_CODE] ?: "ru"
    }.first()

    suspend fun setUseActionColors(use: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USE_ACTION_COLORS] = use
        }
    }

    suspend fun setCardAlpha(alpha: Float) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.CARD_ALPHA] = alpha
        }
    }

    suspend fun setUseTickersInMatrix(use: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USE_TICKERS_IN_MATRIX] = use
        }
    }
}