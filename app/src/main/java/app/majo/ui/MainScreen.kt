package app.majo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.majo.domain.repository.ActionRepository
import app.majo.ui.screens.action_list.ActionListScreen
import app.majo.ui.screens.action_list.ActionListViewModel
import app.majo.ui.screens.add_action.ActionListViewModelFactory
import app.majo.ui.screens.add_action.AddActionViewModel
import app.majo.ui.screens.add_action.AddActionViewModelFactory
import app.majo.ui.screens.add_action.AddActivityScreen

/**
 * Главный экран-обертка.
 * Содержит в себе Нижний Бар (BottomBar) и Область контента (NavHost).
 */
@Composable
fun MainScreen(
    repository: ActionRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        // --- НИЖНИЙ БАР ---
        bottomBar = {
            BottomAppBar(
                // Вырез под центральную кнопку
                actions = {
                    // Кнопка СЛЕВА - Домой (Список)
                    IconButton(onClick = {
                        // Возврат на главный экран, сбрасывая стек
                        navController.navigate("activities") {
                            popUpTo("activities") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.Home, contentDescription = "Список")
                    }

                    // Распорка, чтобы отодвинуть остальные кнопки вправо
                    // Но так как у нас есть FAB в центре, BottomAppBar сам раздвинет их,
                    // если мы используем Spacer(Modifier.weight(1f))
                    Box(Modifier.weight(1f))

                    // Кнопки СПРАВА

                    // 1. Создать новый ТИП активности (бывший "+")
                    IconButton(onClick = { navController.navigate("addActivity") }) {
                        Icon(Icons.Filled.Create, contentDescription = "Новый тип")
                    }

                    // 2. Настройки (Заглушка)
                    IconButton(onClick = {
                        Toast.makeText(context, "Настройки (скоро)", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                    }
                },
                // ЦЕНТРАЛЬНАЯ КНОПКА (FAB)
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            Toast.makeText(context, "Добавить запись (скоро)", Toast.LENGTH_SHORT).show()
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        shape = CircleShape, // Круглая
                        modifier = Modifier.size(64.dp) // Чуть больше стандартной
                    ) {
                        Icon(Icons.Filled.Add, "Добавить запись", modifier = Modifier.size(32.dp))
                    }
                }
            )
        }
    ) { innerPadding ->

        // --- ОБЛАСТЬ КОНТЕНТА ---
        NavHost(
            navController = navController,
            startDestination = "activities",
            modifier = Modifier.padding(innerPadding) // Важно! Учитываем отступы бара
        ) {
            // 1. Экран списка
            composable("activities") {
                val vm: ActionListViewModel = viewModel(
                    factory = ActionListViewModelFactory(repository)
                )
                // Мы убрали onAddClick, так как кнопка теперь в BottomBar
                ActionListScreen(
                    viewModel = vm,
                    onItemClick = { id ->
                        navController.navigate("editActivity/$id")
                    }
                )
            }

            // 2. Экран добавления типа
            composable("addActivity") {
                val vm: AddActionViewModel = viewModel(
                    factory = AddActionViewModelFactory(repository)
                )
                AddActivityScreen(
                    viewModel = vm,
                    actionId = null,
                    onSaved = { navController.popBackStack() }
                )
            }

            // 3. Экран редактирования
            composable("editActivity/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")!!.toLong()
                val vm: AddActionViewModel = viewModel(
                    factory = AddActionViewModelFactory(repository)
                )
                AddActivityScreen(
                    viewModel = vm,
                    actionId = id,
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}