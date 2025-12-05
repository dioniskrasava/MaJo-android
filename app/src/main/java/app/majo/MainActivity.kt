package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.viewModels // <-- НОВЫЙ ИМПОРТ
import app.majo.data.local.database.AppDatabaseInstance
import app.majo.data.repository.ActionRepositoryImpl
import app.majo.ui.MainScreen
import app.majo.ui.screens.settings.SettingsViewModel // <-- НОВЫЙ ИМПОРТ
import app.majo.ui.theme.MaJoTheme // <-- УБЕДИСЬ, ЧТО ИМПОРТ ТЕМЫ ПРАВИЛЬНЫЙ

class MainActivity : ComponentActivity() {

    // 1. Создаем единственный экземпляр ViewModel для настроек,
    // привязанный к жизненному циклу Activity
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // --- ТВОЯ ОРИГИНАЛЬНАЯ ЛОГИКА ROOM (ОСТАВЛЯЕМ ЕЕ!) ---
            val context = LocalContext.current
            val database = remember { AppDatabaseInstance.getDatabase(context) }
            val actionDao = database.actionDao()
            val activityRepo = remember { ActionRepositoryImpl(actionDao) }

            // 2. Используем MaJoTheme и передаем в него наш общий ViewModel
            MaJoTheme(settingsViewModel = settingsViewModel) {

                // 3. Передаем обе зависимости в MainScreen
                MainScreen(
                    repository = activityRepo,
                    settingsViewModel = settingsViewModel // <-- ПЕРЕДАЕМ VM
                )
            }
        }
    }
}