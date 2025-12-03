package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.majo.data.local.database.AppDatabaseInstance
import app.majo.data.repository.ActionRepositoryImpl
import app.majo.ui.MainScreen // Импортируем наш новый экран
import app.majo.ui.theme.MaJoandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaJoandroidTheme {
                val context = LocalContext.current
                val database = remember { AppDatabaseInstance.getDatabase(context) }
                val actionDao = database.actionDao()
                val activityRepo = remember { ActionRepositoryImpl(actionDao) }

                // Вся логика навигации теперь внутри MainScreen
                MainScreen(repository = activityRepo)
            }
        }
    }
}