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
    isVertical: Boolean
) {
    val viewModel: MatrixViewModel = viewModel(
        factory = MatrixViewModelFactory(actionRepository, recordRepository)
    )
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
            // Панель переключения месяцев
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::prevMonth) {
                    Text("<")
                }
                Text(
                    text = monthFormatter.format(Date(state.monthStart)),
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = viewModel::setCurrentMonth) {
                        Text(stringResource(R.string.today_short))
                    }
                    IconButton(onClick = viewModel::nextMonth) {
                        Text(">")
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (isVertical) {
                    MatrixScreenVertical(
                        state = state,
                        useTickers = useTickers,
                        leftColumnWidth = leftColumnWidth,
                        onSquareClick = onSquareClick,
                        modifier = Modifier.weight(1f)   // занимаем оставшееся пространство
                    )
                } else {
                    HorizontalMatrixContent(
                        state = state,
                        useTickers = useTickers,
                        leftColumnWidth = leftColumnWidth,
                        dayFormatter = dayFormatter,
                        onSquareClick = onSquareClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MatrixScreenVertical(
    state: MatrixState,
    useTickers: Boolean,
    leftColumnWidth: Dp,
    onSquareClick: (Long?, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayCellSize = 40.dp
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
    dayFormatter: SimpleDateFormat,
    onSquareClick: (Long?, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()
    Box(
        modifier = modifier   // применяем переданный модификатор (с весом)
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .padding(horizontal = 8.dp)
        ) {
            // Заголовки дней
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Spacer(modifier = Modifier.width(leftColumnWidth))
                state.days.forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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
                                .size(40.dp)
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
}