package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.majo.data.local.database.AppDatabaseInstance
import app.majo.data.repository.ActionRepositoryImpl
import app.majo.data.repository.FakeActionRepository
import app.majo.domain.repository.ActionRepository
import app.majo.ui.MainScreen // Импортируем наш новый экран
import app.majo.ui.screens.settings.SettingsViewModel
import app.majo.ui.theme.MaJoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Создаем единственный экземпляр ViewModel для настроек
        // Используем стандартную функцию by viewModels()
        // (убедись, что импортировано androidx.activity.viewModels)
        val settingsViewModel: SettingsViewModel by viewModels()

        // ВАЖНО: Тебе также понадобится ActionRepository, который ты уже используешь
        val actionRepository: ActionRepository by lazy {
            // ... Здесь должно быть получение твоего ActionRepository (Room или Fake)
            // Я предполагаю, что у тебя уже есть доступ к нему в MainActivity
            // Используем заглушку, чтобы код был полным
            FakeActionRepository() // Или ActionRepositoryImpl(database.dao)
        }

        setContent {
            // 2. Передаем ЕДИНЫЙ settingsViewModel в MaJoTheme
            MaJoTheme(settingsViewModel = settingsViewModel) {

                // 3. Передаем ЕДИНЫЙ settingsViewModel в MainScreen,
                // чтобы он мог быть передан в SettingsScreen
                MainScreen(
                    repository = actionRepository,
                    settingsViewModel = settingsViewModel // <-- НОВЫЙ АРГУМЕНТ
                )
            }
        }
    }
}