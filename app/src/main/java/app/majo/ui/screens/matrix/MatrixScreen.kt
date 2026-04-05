package app.majo.ui.screens.matrix

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.R
import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionType
import app.majo.domain.repository.ActionRepository
import app.majo.domain.repository.RecordRepository
import app.majo.ui.common.SimpleTopAppBar
import app.majo.ui.screens.settings.MatrixPeriodType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(
    actionRepository: ActionRepository,
    recordRepository: RecordRepository,
    onNavigateBack: () -> Unit,
    onSquareClick: (Long?, Long, Long) -> Unit,
    onSettingsClick: () -> Unit,
    useTickers: Boolean,
    cellSize: Int,
    periodType: MatrixPeriodType,
    onAddBinaryRecord: (Long, Double, Long) -> Unit
) {
    val viewModel: MatrixViewModel = viewModel(
        factory = MatrixViewModelFactory(actionRepository, recordRepository)
    )

    // Обновляем тип периода при изменении настройки
    LaunchedEffect(periodType) {
        viewModel.updatePeriodType(periodType)
    }

    val state by viewModel.state.collectAsState()
    val monthFormatter = remember { SimpleDateFormat("LLLL yyyy", Locale("ru")) }
    val dayFormatter = remember { SimpleDateFormat("d", Locale.getDefault()) }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodyMedium
    val maxTextWidth = remember(state.actions, useTickers) {
        var max = 0
        state.actions.forEach { action ->
            val text = if (useTickers && action.ticker.isNotBlank()) action.ticker else action.name
            val width = textMeasurer.measure(text, style).size.width
            if (width > max) max = width
        }
        max
    }
    val leftColumnWidth = with(density) { maxTextWidth.toDp() + 16.dp }


    var showBinaryDialog by remember { mutableStateOf(false) }
    var pendingBinaryActionId by remember { mutableStateOf<Long?>(null) }
    var pendingBinaryDayStart by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = stringResource(R.string.matrix_title),
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.matrix_settings))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Панель переключения периодов (заголовок с названием месяца/недели)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::prevPeriod) {  // используем общий prevPeriod
                    Text("<")
                }
                Text(
                    // Формируем заголовок в зависимости от типа периода
                    text = when (state.periodType) {
                        MatrixPeriodType.MONTH -> monthFormatter.format(Date(state.startDate))
                        MatrixPeriodType.WEEK_CALENDAR, MatrixPeriodType.WEEK_ROLLING -> {
                            val start = Date(state.startDate)
                            val end = Date(state.startDate + 6 * 24 * 60 * 60 * 1000L)
                            "${dayFormatter.format(start)} – ${dayFormatter.format(end)}"
                        }
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = viewModel::setCurrentPeriod) {
                        Text(stringResource(R.string.today_short))
                    }
                    IconButton(onClick = viewModel::nextPeriod) {
                        Text(">")
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Всегда горизонтальный режим
                HorizontalMatrixContent(
                    state = state,
                    useTickers = useTickers,
                    leftColumnWidth = leftColumnWidth,
                    dayCellSize = cellSize.dp,
                    dayFormatter = dayFormatter,
                    onSquareClick = onSquareClick,
                    modifier = Modifier.weight(1f),
                    onBinaryAction = { actionId, dayStart ->
                        pendingBinaryActionId = actionId
                        pendingBinaryDayStart = dayStart
                        showBinaryDialog = true
                    }

                )
            }


        }

        // Диалог бинарного ввода
        if (showBinaryDialog && pendingBinaryActionId != null && pendingBinaryDayStart != null) {
            BinaryRecordDialog(
                action = state.actions.find { it.id == pendingBinaryActionId }!!,
                dayStart = pendingBinaryDayStart!!,
                onDismiss = { showBinaryDialog = false },
                onConfirm = { value ->
                    // Создаём запись через переданный колбэк
                    onAddBinaryRecord(pendingBinaryActionId!!, value, pendingBinaryDayStart!!)
                    showBinaryDialog = false
                },
                onStandardInput = {
                    showBinaryDialog = false
                    // Переходим на экран добавления записи с выбранной датой и активностью
                    onSquareClick(null, pendingBinaryActionId!!, pendingBinaryDayStart!!)
                }
            )
        }

    }

}


@Composable
fun HorizontalMatrixContent(
    state: MatrixState,
    useTickers: Boolean,
    leftColumnWidth: Dp,
    dayCellSize: Dp,
    dayFormatter: SimpleDateFormat,
    onSquareClick: (Long?, Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    onBinaryAction: (Long, Long) -> Unit,
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScrollState)
            .padding(horizontal = 8.dp)
    ) {
        // Заголовок дней
        Row(
            modifier = Modifier.horizontalScroll(horizontalScrollState)
        ) {
            Spacer(modifier = Modifier.width(leftColumnWidth))
            state.days.forEach { day ->
                Box(
                    modifier = Modifier
                        .size(dayCellSize)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayFormatter.format(Date(day)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Строки действий
        state.actions.forEach { action ->
            Row(
                modifier = Modifier.horizontalScroll(horizontalScrollState)
            ) {
                Box(modifier = Modifier.width(leftColumnWidth).padding(end = 4.dp)) {
                    Text(
                        text = if (useTickers && action.ticker.isNotBlank()) action.ticker else action.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                state.days.forEach { dayStart ->
                    val key = Pair(action.id, dayStart)
                    val recordInfo = state.recordInfo[key]
                    val recordId = recordInfo?.first
                    val points = recordInfo?.second ?: 0.0

                    val color = when {
                        recordId == null -> Color.LightGray
                        points < 0 -> Color(0xFFFF5252)
                        else -> Color(0xFF4CAF50)
                    }

                    Box(
                        modifier = Modifier
                            .size(dayCellSize)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .clickable {
                                if (action.type == ActionType.BINARY && recordId == null) {
                                    onBinaryAction(action.id, dayStart)
                                } else {
                                    onSquareClick(recordId, action.id, dayStart)
                                }
                            }
                    )
                }
            }
        }
    }
}

// Бинарная запись (диалог)
@Composable
fun BinaryRecordDialog(
    action: Action,
    dayStart: Long,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    onStandardInput: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Заголовок
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.titleLarge
                )
                // Текст
                Text(
                    text = "Выберите значение для ${formatDate(dayStart)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Первая строка: Да / Нет
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onConfirm(1.0) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Да", color = Color.White)
                    }
                    Button(
                        onClick = { onConfirm(-1.0) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Нет", color = Color.White)
                    }
                }
                // Вторая строка: Отмена (серая кнопка)
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
                // Третья строка: Стандартный ввод
                TextButton(
                    onClick = onStandardInput,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Стандартный ввод")
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}