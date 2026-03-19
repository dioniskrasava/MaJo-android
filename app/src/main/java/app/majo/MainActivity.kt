package app.majo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
//import androidx.activity.viewModels // Импорт для делегата by viewModels()
import androidx.lifecycle.viewmodel.compose.viewModel // Добавляем импорт
import app.majo.data.local.database.AppDatabaseInstance
import app.majo.data.repository.ActionRepositoryImpl
import app.majo.ui.MainScreen
import app.majo.ui.screens.settings.SettingsViewModel // ViewModel для управления настройками
import app.majo.ui.screens.settings.SettingsViewModelFactory // Добавляем импорт
import app.majo.ui.theme.MaJoTheme // Корневой Composable для применения темы
import app.majo.data.local.datastore.SettingsDataStore // Добавляем импорт

import app.majo.data.repository.RecordRepositoryImpl // <-- Добавлен импорт
import app.majo.domain.repository.ActionRepository // Добавлен импорт для явного указания типа
import app.majo.domain.repository.RecordRepository // <-- Добавлен импорт для явного указания типа
import app.majo.ui.shared.SharedRecordsViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale


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


        var isReady by mutableStateOf(false)
        var languageCode by mutableStateOf("ru")

        lifecycleScope.launch {
            languageCode = SettingsDataStore(this@MainActivity).getLanguageCode()
            applyLocale(languageCode)
            isReady = true
        }


        // Включает режим "от края до края" (Edge-to-Edge), используя всю площадь экрана
        enableEdgeToEdge()
        setContent {

            if (isReady) {
                // Ваш основной контент
                // --- Инициализация зависимостей (Repository Layer) ---
                val context = LocalContext.current

                // Получаем или создаем экземпляр базы данных Room
                val database = remember { AppDatabaseInstance.getDatabase(context) }

                // Получаем DAO
                val actionDao = database.actionDao()

                // Получаем DAO для записей
                val recordDao = database.recordDao()

                // Создаем реализацию репозитория, используя DAO
                val activityRepo = remember { ActionRepositoryImpl(actionDao) }

                val recordRepo: RecordRepository = remember { RecordRepositoryImpl(recordDao) }

                // Создаем DataStore
                val settingsDataStore = remember { SettingsDataStore(context) }

                // Создаем SettingsViewModel с помощью Factory
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(settingsDataStore, activityRepo, recordRepo)
                )

                val sharedRecordsViewModel: SharedRecordsViewModel = viewModel() // для передачи даты выбранного дня

                // 2. Применение Темы
                // MaJoTheme использует settingsViewModel для реактивного переключения темы.
                MaJoTheme(settingsViewModel = settingsViewModel) {

                    // 3. Корневой Экран
                    // MainScreen получает все необходимые зависимости для дальнейшей передачи в NavHost.
                    MainScreen(
                        actionRepository = activityRepo,
                        recordRepository = recordRepo,
                        settingsViewModel = settingsViewModel, // Передача общего VM в NavHost для SettingsScreen
                        sharedRecordsViewModel = sharedRecordsViewModel
                    )
                }
            } else {
                // Показываем загрузку
                Text("Download...")
            }


        }
    }



    fun Context.applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }


}



fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Context is not an Activity")
}