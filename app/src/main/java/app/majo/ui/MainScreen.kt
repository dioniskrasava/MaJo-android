package app.majo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.BottomAppBar 
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.findActivity
import app.majo.ui.screens.action_list.ActionListScreen
import app.majo.ui.screens.action_list.ActionListViewModel
import app.majo.ui.screens.action_list.ActionListViewModelFactory
import app.majo.ui.screens.add_action.AddActionViewModel
import app.majo.ui.screens.add_action.AddActionViewModelFactory
import app.majo.ui.screens.add_action.AddActivityScreen
import app.majo.ui.screens.settings.SettingsScreen
import app.majo.ui.screens.settings.SettingsViewModel
import app.majo.ui.screens.add_record.AddRecordViewModel
import app.majo.ui.screens.add_record.AddRecordViewModelFactory
import app.majo.ui.screens.add_record.AddRecordScreen
import app.majo.ui.screens.record_list.RecordListScreen
import app.majo.ui.util.Screen
import app.majo.ui.screens.record_list.RecordListViewModel
import app.majo.ui.screens.record_list.RecordListViewModelFactory
import app.majo.ui.shared.SharedRecordsViewModel
import app.majo.R
import app.majo.ui.screens.help_topics.HelpDetailScreen
import app.majo.ui.screens.help_topics.HelpTopicsScreen
import app.majo.ui.screens.logs.LogsScreen
import app.majo.ui.screens.logs.LogsViewModel
import app.majo.ui.screens.logs.LogsViewModelFactory
import app.majo.ui.screens.matrix.MatrixScreen
import app.majo.ui.screens.matrix.MatrixViewModel
import app.majo.ui.screens.matrix.MatrixViewModelFactory
import app.majo.ui.screens.matrix_setting.MatrixSettingsScreen
import app.majo.ui.screens.ticker_setting.TickerSettingsScreen
import app.majo.ui.screens.ticker_setting.TickerSettingsViewModel
import app.majo.ui.screens.ticker_setting.TickerSettingsViewModelFactory
import app.majo.ui.screens.statistics.StatisticsScreen
import app.majo.ui.screens.statistics.StatisticsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import app.majo.domain.model.record.ActionRecord
import kotlinx.coroutines.launch


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
    val navItems = listOf(Screen.Records, Screen.Activities, Screen.Statistics, Screen.Settings)

    // НОВЫЙ КОД: Сохраняем только СТРОКУ маршрута, которая поддерживается rememberSaveable.
    var selectedRoute by rememberSaveable { mutableStateOf(Screen.Records.route) } 

    val currentDayStart by sharedRecordsViewModel.currentDayStartMs.collectAsState()


    // Для бинарного ввода записи
    val scope = rememberCoroutineScope()

    val onAddBinaryRecord: (Long, Double, Long) -> Unit = { actionId, value, dayStart ->
        scope.launch {
            val action = actionRepository.getActionById(actionId)
            if (action != null) {
                val totalPoints = value * action.pointsPerUnit
                val record = ActionRecord(
                    id = 0,
                    activityId = actionId,
                    value = value,
                    timestamp = dayStart,
                    totalPoints = totalPoints
                )
                recordRepository.insert(record)
            }
        }
    }


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
                modifier = Modifier.size(62.dp)
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_record_title), modifier = Modifier.size(32.dp))
            }
        },

        // 2. НИЖНИЙ БАР (BottomAppBar)
        bottomBar = {
            BottomAppBar(
                // Устанавливаем цвет фона всей панели
                containerColor = MaterialTheme.colorScheme.primary,
                // (Опционально) Цвет контента, если нужно перекрасить всё сразу
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                navItems.forEach { screen ->
                    // ИЗМЕНЕНИЕ 1: Сравниваем выбранный маршрут с маршрутом текущего экрана
                    val isSelected = selectedRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.titleRes)) },
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
                    sharedViewModel = sharedRecordsViewModel,
                    settingsViewModel = settingsViewModel,
                    onRecordClick = { recordId ->
                        navController.navigate("edit_record/$recordId")
                    },
                    onLogsClick = {
                        navController.navigate(Screen.Logs.route)
                    },
                    onMatrixClick = { navController.navigate("matrix") }
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
                    settingsViewModel = settingsViewModel,
                    onItemClick = { id ->
                        navController.navigate("editActivity/$id")
                    },
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
                    onNavigateBack = { navController.popBackStack() },
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
                    onNavigateBack = { navController.popBackStack() },
                    actionId = id, // Сигнал для ViewModel, что это режим редактирования
                    onSaved = { navController.popBackStack() } // Закрываем экран после сохранения/удаления
                )
            }

            // МАРШРУТ 5: Экран настроек
            composable("settings") {
                val context = LocalContext.current
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = settingsViewModel,
                    onLanguageChange = {
                        context.findActivity().recreate()   // перезапуск после сохранения языка
                    },
                    onNavigateToHelp = { navController.navigate("help_topics") }
                )
            }



            // Экран добавления записи
            composable(
                route = "add_record?selectedDate={selectedDate}&activityId={activityId}",
                arguments = listOf(
                    navArgument("selectedDate") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("activityId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { backStackEntry ->
                val selectedDate = backStackEntry.arguments?.getLong("selectedDate") ?: -1L
                val activityId = backStackEntry.arguments?.getLong("activityId") ?: -1L
                val vm: AddRecordViewModel = viewModel(
                    factory = AddRecordViewModelFactory(actionRepository, recordRepository)
                )
                LaunchedEffect(selectedDate, activityId) {
                    if (selectedDate != -1L) {
                        vm.updateTimestamp(selectedDate)
                    }
                    if (activityId != -1L && !vm.isEditMode.value) {
                        vm.selectActionById(activityId) // нужно добавить этот метод
                    }
                }
                AddRecordScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }


            // маршрут для редактирования записи
            composable(
                route = "edit_record/{recordId}",
                arguments = listOf(navArgument("recordId") { type = NavType.LongType })
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getLong("recordId") ?: return@composable
                val vm: AddRecordViewModel = viewModel(
                    factory = AddRecordViewModelFactory(actionRepository, recordRepository)
                )
                LaunchedEffect(recordId) {
                    vm.loadRecord(recordId)
                }
                AddRecordScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Logs.route) {
                val vm: LogsViewModel = viewModel(
                    factory = LogsViewModelFactory(actionRepository, recordRepository)
                )
                LogsScreen(
                    viewModel = vm,
                    settingsViewModel = settingsViewModel,
                    onRecordClick = { recordId ->
                        navController.navigate("edit_record/$recordId")
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // В MainScreen, внутри NavHost, после composable(Screen.Logs.route) добавить:
            composable("matrix") {
                val settingsState by settingsViewModel.state.collectAsState()
                MatrixScreen(
                    actionRepository = actionRepository,
                    recordRepository = recordRepository,
                    onNavigateBack = { navController.popBackStack() },
                    onSquareClick = { recordId, activityId, dayStart ->
                        if (recordId != null) {
                            navController.navigate("edit_record/$recordId")
                        } else {
                            navController.navigate("add_record?selectedDate=$dayStart&activityId=$activityId")
                        }
                    },
                    onSettingsClick = { navController.navigate("matrixSettings") },
                    useTickers = settingsState.useTickersInMatrix,
                    cellSize = settingsState.matrixCellSize,
                    periodType = settingsState.matrixPeriodType,
                    onAddBinaryRecord = onAddBinaryRecord
                )
            }


            composable("matrixSettings") {
                MatrixSettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onConfigureTickers = { navController.navigate("tickerSettings") }
                )
            }

            composable("tickerSettings") {
                val vm: TickerSettingsViewModel = viewModel(
                    factory = TickerSettingsViewModelFactory(actionRepository)
                )
                TickerSettingsScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }


            // СПРАВКИ

            composable("help_topics") {
                HelpTopicsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTopicClick = { topicId ->
                        navController.navigate("help_detail/$topicId")
                    }
                )
            }
            composable("help_detail/{topicId}") { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                HelpDetailScreen(
                    topicId = topicId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // СТАТИСТИКА

            composable(Screen.Statistics.route) {
                val vm: StatisticsViewModel = hiltViewModel() // Hilt сам предоставит экземпляр
                StatisticsScreen(
                    viewModel = vm,
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }




        }
    }
}