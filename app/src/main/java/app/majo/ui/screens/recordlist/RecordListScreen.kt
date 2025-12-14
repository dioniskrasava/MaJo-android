package app.majo.ui.screens.recordlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.ui.components.getActionIcon
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel
) {
    val recordsMap by viewModel.recordsWithActivities.collectAsState()
    val currentDayStart by viewModel.currentDayStartMs.collectAsState()

    // Считаем общие очки за выбранный день
    val dayTotalPoints = remember(recordsMap) {
        recordsMap.keys.sumOf { it.totalPoints }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Журнал") },
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
                onPrevClick = { viewModel.goToPreviousDay() },
                onNextClick = { viewModel.goToNextDay() }
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
                        text = "Нет записей за этот день",
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
                    // Преобразуем Map в List для LazyColumn.
                    // Сортировка уже идет из БД по убыванию времени (DESC) [cite: 13]
                    items(recordsMap.entries.toList()) { entry ->
                        val record = entry.key
                        val action = entry.value
                        RecordItem(record = record, action = action)
                    }
                }
            }
        }
    }
}

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущий день")
        }

        Text(
            text = if (isToday) "Сегодня" else dateFormatter.format(Date(currentDayStart)),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onNextClick,
            enabled = !isToday // Не даем уйти в будущее, если логика это запрещает
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Следующий день",
                tint = if (isToday) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Итого за день",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = String.format("%.1f", totalPoints),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "очков",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RecordItem(
    record: ActionRecord,
    action: Action?
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка активности (используем хелпер из ActionCard.kt)
            if (action != null) {
                Icon(
                    imageVector = getActionIcon(action.type), //
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                // Если активность была удалена, показываем заглушку
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалено",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Основная информация
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action?.name ?: "Удаленная активность",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = timeFormatter.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Значения и очки
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
                        text = "$valueText ${action.unit.name.lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}