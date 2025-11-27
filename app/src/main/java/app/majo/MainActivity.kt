package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.ui.theme.MaJoandroidTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.majo.data.repository.FakeActivityRepository
import app.majo.data.repository.FakeRecordRepository
import app.majo.domain.repository.ActivityRepository
import app.majo.ui.screens.activity_list.ActivityListScreen
import app.majo.ui.screens.activity_list.ActivityListViewModel
import app.majo.ui.screens.add_activity.ActivityListViewModelFactory
import app.majo.ui.screens.add_activity.AddActivityScreen
import app.majo.ui.screens.add_activity.AddActivityViewModel
import app.majo.ui.screens.add_activity.AddActivityViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaJoandroidTheme {

                val activityRepo = remember { FakeActivityRepository() }
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "activities"
                ) {
                    composable("activities") {

                        val vm: ActivityListViewModel = viewModel(
                            factory = ActivityListViewModelFactory(activityRepo)
                        )

                        ActivityListScreen(
                            viewModel = vm,
                            onAddClick = { navController.navigate("addActivity") },
                            onItemClick = { id ->
                                // позже сделаем: navController.navigate("activity/$id")
                            }
                        )
                    }

                    composable("addActivity") {

                        val vm: AddActivityViewModel = viewModel(
                            factory = AddActivityViewModelFactory(activityRepo)
                        )

                        AddActivityScreen(
                            viewModel = vm,
                            onSaved = { navController.popBackStack() }
                        )
                    }
                }
            }
        }


    }
}

