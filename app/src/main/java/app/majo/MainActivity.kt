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
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.ui.MainScreen
import app.majo.ui.screens.settings.SettingsViewModel
import app.majo.ui.screens.settings.SettingsViewModelFactory
import app.majo.ui.theme.MaJoTheme
import app.majo.data.local.datastore.SettingsDataStore
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.ui.shared.SharedRecordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


/**
 * Единственная Activity в приложении (Single Activity Architecture).
 *
 * Отвечает за:
 * 1. Инициализацию глобальных системных настроек (например, EdgeToEdge).
 * 2. Создание глобальных зависимостей (база данных, репозиторий).
 * 3. Создание ViewModel, привязанной к жизненному циклу Activity (SettingsViewModel).
 * 4. Установку корневого Composable-дерева ([MaJoTheme] и [MainScreen]).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var actionRepository: ActionRepository

    @Inject
    lateinit var recordRepository: RecordRepository

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

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
                val settingsFactory = SettingsViewModelFactory(settingsDataStore, actionRepository, recordRepository)
                val settingsViewModel: SettingsViewModel = viewModel(factory = settingsFactory)

                val sharedRecordsViewModel: SharedRecordsViewModel = viewModel()

                MaJoTheme(settingsViewModel = settingsViewModel) {
                    MainScreen(
                        actionRepository = actionRepository,
                        recordRepository = recordRepository,
                        settingsViewModel = settingsViewModel,
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