package app.majo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
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
 */
@Composable
fun MainScreen(
    repository: ActionRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        // 1. КНОПКА (FAB)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Добавить запись (скоро)", Toast.LENGTH_SHORT).show()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                shape = CircleShape,
                modifier = Modifier
                    .size(76.dp)
                    // ↓↓↓ ВОТ ГЛАВНОЕ ИЗМЕНЕНИЕ ↓↓↓
                    // Сдвигаем кнопку вниз на 45dp, чтобы она "села" на бар
                    // Можешь менять это число, чтобы настроить глубину посадки
                    .offset(y = 75.dp)
            ) {
                Icon(Icons.Filled.Add, "Добавить запись", modifier = Modifier.size(32.dp))
            }
        },
        // Позиция по центру (по умолчанию она НАД баром)
        floatingActionButtonPosition = FabPosition.Center,

        // 2. НИЖНИЙ БАР
        bottomBar = {
            BottomAppBar(
                // Отключаем свой паддинг, чтобы мы могли управлять расположением сами
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                actions = {
                    // Чтобы иконки были красиво распределены, используем Row внутри
                    // и Spacer-ы

                    // Левая кнопка
                    IconButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = {
                            navController.navigate("activities") {
                                popUpTo("activities") { inclusive = true }
                            }
                        }) {
                        Icon(Icons.Filled.Home, contentDescription = "Список")
                    }

                    // Центральная распорка (отодвигает левые и правые кнопки)
                    Spacer(Modifier.weight(1f))

                    // Правые кнопки
                    IconButton(onClick = { navController.navigate("addActivity") }) {
                        Icon(Icons.Filled.Create, contentDescription = "Новый тип")
                    }

                    IconButton(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = {
                            Toast.makeText(context, "Настройки (скоро)", Toast.LENGTH_SHORT).show()
                        }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                    }
                }
            )
        }
    ) { innerPadding ->

        // --- ОБЛАСТЬ КОНТЕНТА ---
        NavHost(
            navController = navController,
            startDestination = "activities",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("activities") {
                val vm: ActionListViewModel = viewModel(
                    factory = ActionListViewModelFactory(repository)
                )
                ActionListScreen(
                    viewModel = vm,
                    onItemClick = { id ->
                        navController.navigate("editActivity/$id")
                    }
                )
            }

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