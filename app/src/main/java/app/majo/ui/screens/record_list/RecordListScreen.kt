package app.majo.ui.screens.record_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.ui.components.getActionIcon
import app.majo.ui.shared.SharedRecordsViewModel
import app.majo.ui.util.atStartOfDay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import app.majo.R
import app.majo.ui.screens.settings.SettingsViewModel
import app.majo.ui.theme.getColorByName
import app.majo.ui.util.toLocalizedString
import androidx.compose.material3.MaterialTheme
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel,
    sharedViewModel: SharedRecordsViewModel,
    settingsViewModel: SettingsViewModel,
    onRecordClick: (Long) -> Unit,
    onLogsClick: () -> Unit,
    onMatrixClick: () -> Unit
) {
    val recordsMap by viewModel.recordsWithActivities.collectAsState()
    val currentDayStart by viewModel.currentDayStartMs.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    // Считаем общие очки за выбранный день
    val dayTotalPoints = remember(recordsMap) {
        recordsMap.keys.sumOf { it.totalPoints }
    }

    val settingsState by settingsViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        val sharedDate = sharedViewModel.currentDayStartMs.value
        if (viewModel.currentDayStartMs.value != sharedDate) {
            viewModel.setDay(sharedDate)  // добавим этот метод
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.journal)) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logs_title)) },
                            onClick = {
                                menuExpanded = false
                                onLogsClick()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.matrix_title)) },
                            onClick = {
                                menuExpanded = false
                                // предполагаем, что onLogsClick теперь заменён на более общий вызов
                                // или добавим новый callback onMatrixClick
                                onMatrixClick() // нужно добавить в параметры
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Навигация по датам
            DateControlBar(
                currentDayStart = currentDayStart,
                onPrevClick = {
                    val newDate = currentDayStart - TimeUnit.DAYS.toMillis(1)
                    viewModel.goToPreviousDay()               // обновляем VM
                    sharedViewModel.updateCurrentDayStartMs(newDate) // обновляем shared
                },
                onNextClick = {
                    val now = System.currentTimeMillis().atStartOfDay()
                    if (currentDayStart < now) {
                        val newDate = currentDayStart + TimeUnit.DAYS.toMillis(1)
                        viewModel.goToNextDay()
                        sharedViewModel.updateCurrentDayStartMs(newDate)
                    }
                }
            )

            // 2. Карточка с итогом за день
            DaySummaryCard(totalPoints = dayTotalPoints)

            // 3. Список записей
            if (recordsMap.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_records_for_day),                 //  "Нет записей за этот день"
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp) // Отступ для FAB
                ) {
                    items(recordsMap.entries.toList()) { entry ->
                        val record = entry.key
                        val action = entry.value
                        RecordItem(
                            record = record,
                            action = action,
                            useColors = settingsState.useActionColors,   // передаём флаг
                            cardAlpha = settingsState.cardAlpha,
                            onClick = { onRecordClick(record.id) }
                        )
                    }
                }
            }
        }
    }
}

/** ДАТА ЭКРАНА ЗАПИСЕЙ*/
@Composable
fun DateControlBar(
    currentDayStart: Long,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy", Locale("ru")) }
    val isToday = remember(currentDayStart) {
        val now = System.currentTimeMillis()
        // Простая проверка, является ли это сегодняшним днем (можно улучшить через Calendar)
        val diff = now - currentDayStart
        diff in 0 until 86400000 // 24 часа
    }



    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.previous_day))
            }

            Text(
                text = if (isToday) stringResource(R.string.today) else dateFormatter.format(Date(currentDayStart)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onNextClick,
                enabled = !isToday // Не даем уйти в будущее, если логика это запрещает
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.next_day),
                    tint = if (isToday) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DaySummaryCard(totalPoints: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.total_for_day),   // "Итого за день"
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = String.format("%.1f", totalPoints),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(R.string.points),   // "очков"
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun RecordItem(
    record: ActionRecord,
    action: Action?,
    useColors: Boolean,
    cardAlpha: Float,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLight = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
    val iconColor = if (useColors && action != null) {
        //getColorByName(action.color, isLight) // берем кастомный цвет активности
        MaterialTheme.colorScheme.primary // пока поменяем на праймари. кажется что цвет карточки это цвет карточки, а не еще цвет иконки (иначе иногда не видно)
    } else {
        MaterialTheme.colorScheme.primary
    }

    val backgroundColor = if (useColors && action != null) {
        getColorByName(action.color, isLight).copy(alpha = cardAlpha)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        //elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (action != null) {
                Icon(
                    imageVector = getActionIcon(action.type),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.deleted_activity),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action?.name ?: stringResource(R.string.deleted_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = timeFormatter.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${String.format("%.1f", record.totalPoints)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (action != null) {
                    val valueText = if (record.value % 1.0 == 0.0) {
                        record.value.toInt().toString()
                    } else {
                        String.format("%.1f", record.value)
                    }
                    Text(
                        text = "$valueText ${action.unit.toLocalizedString().lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}