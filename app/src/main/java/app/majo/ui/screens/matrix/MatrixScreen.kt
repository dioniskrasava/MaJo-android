package app.majo.ui.screens.matrix

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    onSettingsClick: () -> Unit
) {
    val viewModel: MatrixViewModel = viewModel(
        factory = MatrixViewModelFactory(actionRepository, recordRepository)
    )
    val state by viewModel.state.collectAsState()
    val monthFormatter = remember { SimpleDateFormat("LLLL yyyy", Locale("ru")) }
    val dayFormatter = remember { SimpleDateFormat("d", Locale.getDefault()) }

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
            // Заголовок с месяцем и переключателями
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
                val horizontalScrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                            .padding(horizontal = 8.dp)
                    ) {
                        // Строка с номерами дней
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Spacer(modifier = Modifier.width(100.dp))
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

                        // Строки активностей
                        state.actions.forEach { action ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .padding(end = 4.dp)
                                ) {
                                    Text(
                                        text = action.name,
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
        }
    }
}