package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
//import androidx.activity.viewModels // Импорт для делегата by viewModels()
import androidx.lifecycle.viewmodel.compose.viewModel // Добавляем импорт
import app.majo.data.local.database.AppDatabaseInstance
import app.majo.data.repository.ActionRepositoryImpl
import app.majo.ui.MainScreen
import app.majo.ui.screens.settings.SettingsViewModel // ViewModel для управления настройками
import app.majo.ui.screens.settings.SettingsViewModelFactory // Добавляем импорт
import app.majo.ui.theme.MaJoTheme // Корневой Composable для применения темы
import app.majo.data.local.datastore.SettingsDataStore // Добавляем импорт

/**
 * Единственная Activity в приложении (Single Activity Architecture).
 *
 * Отвечает за:
 * 1. Инициализацию глобальных системных настроек (например, EdgeToEdge).
 * 2. Создание глобальных зависимостей (база данных, репозиторий).
 * 3. Создание ViewModel, привязанной к жизненному циклу Activity (SettingsViewModel).
 * 4. Установку корневого Composable-дерева ([MaJoTheme] и [MainScreen]).
 */
class MainActivity : ComponentActivity() {

    /**
     * 1. Создание единственного экземпляра [SettingsViewModel].
     *
     * Делегат `by viewModels()` привязывает эту ViewModel к жизненному циклу [MainActivity],
     * что позволяет ей управлять глобальным состоянием (например, темной темой)
     * и быть доступной для всех дочерних экранов.
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Включает режим "от края до края" (Edge-to-Edge), используя всю площадь экрана
        enableEdgeToEdge()
        setContent {

            // --- Инициализация зависимостей (Repository Layer) ---
            val context = LocalContext.current

            // Получаем или создаем экземпляр базы данных Room
            val database = remember { AppDatabaseInstance.getDatabase(context) }

            // Получаем DAO
            val actionDao = database.actionDao()

            // Создаем реализацию репозитория, используя DAO
            val activityRepo = remember { ActionRepositoryImpl(actionDao) }

            // Создаем DataStore
            val settingsDataStore = remember { SettingsDataStore(context) }

            // Создаем SettingsViewModel с помощью Factory
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(settingsDataStore)
            )

            // 2. Применение Темы
            // MaJoTheme использует settingsViewModel для реактивного переключения темы.
            MaJoTheme(settingsViewModel = settingsViewModel) {

                // 3. Корневой Экран
                // MainScreen получает все необходимые зависимости для дальнейшей передачи в NavHost.
                MainScreen(
                    repository = activityRepo,
                    settingsViewModel = settingsViewModel // Передача общего VM в NavHost для SettingsScreen
                )
            }
        }
    }
}