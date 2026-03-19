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
import androidx.lifecycle.viewmodel.compose.viewModel
import app.majo.R
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
    periodType: MatrixPeriodType
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
                    modifier = Modifier.weight(1f)
                )
                }
            }
        }
    }

@Composable
fun MatrixScreenVertical(
    state: MatrixState,
    useTickers: Boolean,
    leftColumnWidth: Dp,
    dayCellSize: Dp,                     // новый параметр
    onSquareClick: (Long?, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = state.days
    val actions = state.actions
    val recordInfo = state.recordInfo
    val dayNumberFormatter = remember { SimpleDateFormat("d", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        // Строка заголовков действий (названия сверху)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            // Пустая ячейка над колонкой дат
            Spacer(modifier = Modifier.width(dayCellSize))
            actions.forEach { action ->
                Box(
                    modifier = Modifier
                        .width(leftColumnWidth)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (useTickers && action.ticker.isNotBlank()) action.ticker else action.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Строки для каждого дня
        days.forEach { dayStart ->
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                // Ячейка с датой
                Box(
                    modifier = Modifier
                        .size(dayCellSize)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayNumberFormatter.format(Date(dayStart)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Квадраты действий
                actions.forEach { action ->
                    val key = Pair(action.id, dayStart)
                    val recordInfoPair = recordInfo[key]
                    val hasRecord = recordInfoPair != null
                    val recordId = recordInfoPair?.first
                    val color = when {
                        hasRecord -> Color(0xFF4CAF50)
                        else -> Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .size(dayCellSize)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .clickable {
                                onSquareClick(recordId, action.id, dayStart)
                            }
                    )
                }
            }
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
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScrollState)   // вертикальная прокрутка для всех строк
            .padding(horizontal = 8.dp)
    ) {
        // Заголовок дней (горизонтальная прокрутка)
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

        // Строки действий (горизонтальная прокрутка синхронизирована)
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
                    val hasRecord = recordInfo != null
                    val recordId = recordInfo?.first
                    val color = when {
                        hasRecord -> Color(0xFF4CAF50)
                        else -> Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .size(dayCellSize)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .clickable {
                                onSquareClick(recordId, action.id, dayStart)
                            }
                    )
                }
            }
        }
    }
}