package app.majo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.ui.theme.MaJoandroidTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.majo.data.repository.FakeActionRepository
import app.majo.ui.screens.action_list.ActionListScreen
import app.majo.ui.screens.action_list.ActionListViewModel
import app.majo.ui.screens.add_action.ActionListViewModelFactory
import app.majo.ui.screens.add_action.AddActivityScreen
import app.majo.ui.screens.add_action.AddActionViewModel
import app.majo.ui.screens.add_action.AddActionViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaJoandroidTheme {

                val activityRepo = remember { FakeActionRepository() }
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "activities"
                ) {
                    composable("activities") {

                        val vm: ActionListViewModel = viewModel(
                            factory = ActionListViewModelFactory(activityRepo)
                        )

                        ActionListScreen(
                            viewModel = vm,
                            onAddClick = { navController.navigate("addActivity") },
                            onItemClick = { id ->
                                // позже сделаем: navController.navigate("activity/$id")
                            }
                        )
                    }

                    composable("addActivity") {

                        val vm: AddActionViewModel = viewModel(
                            factory = AddActionViewModelFactory(activityRepo)
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

