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
import androidx.compose.material3.BottomAppBar 
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.ui.screens.action_list.ActionListScreen
import app.majo.ui.screens.action_list.ActionListViewModel
import app.majo.ui.screens.action_list.ActionListViewModelFactory
import app.majo.ui.screens.add_action.AddActionViewModel
import app.majo.ui.screens.add_action.AddActionViewModelFactory
import app.majo.ui.screens.add_action.AddActivityScreen
import app.majo.ui.screens.settings.SettingsScreen
import app.majo.ui.screens.settings.SettingsViewModel
import app.majo.ui.screens.addrecord.AddRecordViewModel 
import app.majo.ui.screens.addrecord.AddRecordViewModelFactory 
import app.majo.ui.screens.addrecord.AddRecordScreen
import app.majo.ui.screens.recordlist.RecordListScreen
import app.majo.ui.util.Screen
import app.majo.ui.screens.recordlist.RecordListViewModel 
import app.majo.ui.screens.recordlist.RecordListViewModelFactory 
import app.majo.ui.shared.SharedRecordsViewModel

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
    actionRepository: ActionRepository,
    recordRepository: RecordRepository,
    settingsViewModel: SettingsViewModel,
    sharedRecordsViewModel: SharedRecordsViewModel
) {
    // Контроллер навигации. Сохраняет стек экранов.
    val navController = rememberNavController()

    // Состояние для отслеживания выбранной вкладки (Records или Activities или Настройки)
    val navItems = listOf(Screen.Records, Screen.Activities, Screen.Settings)

    // НОВЫЙ КОД: Сохраняем только СТРОКУ маршрута, которая поддерживается rememberSaveable.
    var selectedRoute by rememberSaveable { mutableStateOf(Screen.Records.route) } 

    val currentDayStart by sharedRecordsViewModel.currentDayStartMs.collectAsState()


    Scaffold(
        // 1. ПЛАВАЮЩАЯ КНОПКА (FAB)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_record?selectedDate=$currentDayStart")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                shape = CircleShape,
                modifier = Modifier
                    .size(62.dp)
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

            
            // МАРШРУТ 1: Список ЗАПИСЕЙ (новый главный экран)
            composable(Screen.Records.route) {
                val vm: RecordListViewModel = viewModel(
                    factory = RecordListViewModelFactory(actionRepository, recordRepository)
                )
                RecordListScreen(
                    viewModel = vm,
                    sharedViewModel = sharedRecordsViewModel
                )
            }


           
            // МАРШРУТ 2: Список АКТИВНОСТЕЙ
            composable(Screen.Activities.route) {
                
                // создаем viewModel через спец.ф-ю для создания viewModel объектов
                // используя фабрику в которую мы передаем соответствующие репозитории
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


            // МАРШРУТ 3: Создание активности
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

            // МАРШРУТ 4: Редактирование активности (с аргументом {id})
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

            // МАРШРУТ 5: Экран настроек
            composable("settings") {
                // ВАЖНО: Убедитесь, что импорт SettingsScreen присутствует
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    // settingsViewModel передается в MainScreen как аргумент
                    viewModel = settingsViewModel
                )
            }



            // Экран добавления записи
            composable(
                route = "add_record?selectedDate={selectedDate}", // Обновленный маршрут
                arguments = listOf(
                    navArgument("selectedDate") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val selectedDate = backStackEntry.arguments?.getLong("selectedDate") ?: -1L

                val vm: AddRecordViewModel = viewModel(
                    factory = AddRecordViewModelFactory(actionRepository, recordRepository)
                )

                // Передаем дату в ViewModel при открытии экрана
                LaunchedEffect(selectedDate) {
                    if (selectedDate != -1L) {
                        vm.updateTimestamp(selectedDate)
                    }
                }

                AddRecordScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}