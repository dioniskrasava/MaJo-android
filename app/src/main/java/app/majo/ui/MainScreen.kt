package app.majo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.BottomAppBar // <-- Убедись, что импорт есть
import androidx.compose.material3.NavigationBarItem // <-- Убедись, что импорт есть
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository // <-- Добавлен импорт
import app.majo.ui.screens.action_list.ActionListScreen
import app.majo.ui.screens.action_list.ActionListViewModel
import app.majo.ui.screens.action_list.ActionListViewModelFactory
import app.majo.ui.screens.add_action.AddActionViewModel
import app.majo.ui.screens.add_action.AddActionViewModelFactory
import app.majo.ui.screens.add_action.AddActivityScreen
import app.majo.ui.screens.settings.SettingsScreen
import app.majo.ui.screens.settings.SettingsViewModel

import app.majo.ui.screens.addrecord.AddRecordViewModel // <-- Новый импорт
import app.majo.ui.screens.addrecord.AddRecordViewModelFactory // <-- Новый импорт
import app.majo.ui.screens.addrecord.AddRecordScreen // <-- Новый импорт


import app.majo.ui.screens.recordlist.RecordListScreen

import app.majo.ui.util.Screen

import app.majo.ui.screens.recordlist.RecordListViewModel // <-- ДОБАВИТЬ
import app.majo.ui.screens.recordlist.RecordListViewModelFactory // <-- ДОБАВИТЬ

/**
 * Главный экран-оболочка (Application Shell) приложения.
 *
 * Отвечает за:
 * 1. Предоставление каркаса UI (Scaffold) с нижним баром и FAB.
 * 2. Управление навигацией ([NavHost]) между основными экранами.
 * 3. Оркестрацию зависимостей (ViewModel) для каждого маршрута.
 *
 * @param repository Интерфейс репозитория активностей, необходимый для создания VM.
 * @param settingsViewModel Общий экземпляр ViewModel для настроек,
 * привязанный к жизненному циклу Activity, для управления глобальным состоянием темы.
 */
@Composable
fun MainScreen(
    actionRepository: ActionRepository, // <-- Изменено
    recordRepository: RecordRepository, // <-- NEW: Добавлен новый репозиторий
    settingsViewModel: SettingsViewModel
) {
    // Контроллер навигации. Сохраняет стек экранов.
    val navController = rememberNavController()

    // Состояние для отслеживания выбранной вкладки (Records или Activities или Настройки)
    val navItems = listOf(Screen.Records, Screen.Activities, Screen.Settings)

    // НОВЫЙ КОД: Сохраняем только СТРОКУ маршрута, которая поддерживается rememberSaveable.
    var selectedRoute by rememberSaveable { mutableStateOf(Screen.Records.route) } // Тип String выводится автоматически.



    Scaffold(
        // 1. ПЛАВАЮЩАЯ КНОПКА (FAB)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddRecord.route) // <-- NEW: Переход к новому экрану
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                shape = CircleShape,
                modifier = Modifier
                    .size(62.dp)
                // Комментарий о сдвиге (для визуального выравнивания)
                //.offset(y = 48.dp)
            ) {
                Icon(Icons.Filled.Add, "Добавить запись", modifier = Modifier.size(32.dp))
            }
        },

        // 2. НИЖНИЙ БАР (BottomAppBar)
        bottomBar = {
            BottomAppBar {
                navItems.forEach { screen ->
                    // ИЗМЕНЕНИЕ 1: Сравниваем выбранный маршрут с маршрутом текущего экрана
                    val isSelected = selectedRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        onClick = {
                            // ИЗМЕНЕНИЕ 2: Сохраняем только строку маршрута
                            selectedRoute = screen.route
                            navController.navigate(screen.route) {
                                // Навигационные флаги для переключения между вкладками
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        // --- ОБЛАСТЬ КОНТЕНТА (NavHost) ---
        // NavHost занимает всю область Scaffold, исключая padding, созданный Bottom Bar
        NavHost(
            navController = navController,
            startDestination = Screen.Records.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // НОВЫЙ БЛОК:
// NEW: МАРШРУТ 1: Список ЗАПИСЕЙ (новый главный экран)
            composable(Screen.Records.route) {
                val vm: RecordListViewModel = viewModel(
                    factory = RecordListViewModelFactory(actionRepository, recordRepository)
                )
                RecordListScreen(
                    viewModel = vm
                )
            }


            // МАРШРУТ 2: Список АКТИВНОСТЕЙ (перенесен на второй маршрут)
            // МАРШРУТ 2: Список АКТИВНОСТЕЙ
            composable(Screen.Activities.route) {
                val vm: ActionListViewModel = viewModel(
                    factory = ActionListViewModelFactory(actionRepository, recordRepository)
                )
                ActionListScreen(
                    viewModel = vm,
                    onItemClick = { id ->
                        navController.navigate("editActivity/$id")
                    },
                    // ВОТ ЭТА СТРОКА:
                    onNavigateToAddActivity = { navController.navigate("addActivity") }
                )
            }


            // МАРШРУТ 2: Создание активности
            composable("addActivity") {
                val vm: AddActionViewModel = viewModel(
                    factory = AddActionViewModelFactory(actionRepository)
                )
                AddActivityScreen(
                    viewModel = vm,
                    actionId = null, // Сигнал для ViewModel, что это режим создания
                    onSaved = { navController.popBackStack() } // Закрываем экран после сохранения
                )
            }

            // МАРШРУТ 3: Редактирование активности (с аргументом {id})
            composable("editActivity/{id}") { backStackEntry ->
                // Извлечение аргумента "id" из маршрута и его преобразование
                val id = backStackEntry.arguments?.getString("id")!!.toLong()
                val vm: AddActionViewModel = viewModel(
                    factory = AddActionViewModelFactory(actionRepository)
                )
                AddActivityScreen(
                    viewModel = vm,
                    actionId = id, // Сигнал для ViewModel, что это режим редактирования
                    onSaved = { navController.popBackStack() } // Закрываем экран после сохранения/удаления
                )
            }

            // МАРШРУТ 4: Экран настроек
            composable("settings") {
                // ВАЖНО: Убедитесь, что импорт SettingsScreen присутствует
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    // settingsViewModel передается в MainScreen как аргумент
                    viewModel = settingsViewModel
                )
            }



            composable(Screen.AddRecord.route) {
                val vm: AddRecordViewModel = viewModel(
                    factory = AddRecordViewModelFactory(actionRepository, recordRepository) // <-- Передаем оба репозитория
                )
                AddRecordScreen(
                    viewModel = vm,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}