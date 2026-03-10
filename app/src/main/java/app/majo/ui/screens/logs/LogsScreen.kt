package app.majo.ui.screens.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.majo.R
import app.majo.domain.model.action.Action
import app.majo.domain.model.record.ActionRecord
import app.majo.ui.common.SimpleTopAppBar
import app.majo.ui.components.getActionIcon
import app.majo.ui.util.toLocalizedString
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: LogsViewModel,
    onRecordClick: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val logsMap by viewModel.logsWithActions.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = stringResource(R.string.logs_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        if (logsMap.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_records))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(logsMap.entries.toList()) { (record, action) ->
                    LogsItem(
                        record = record,
                        action = action,
                        onClick = { onRecordClick(record.id) },
                        dateFormatter = dateFormatter
                    )
                }
            }
        }
    }
}

@Composable
fun LogsItem(
    record: ActionRecord,
    action: Action?,
    onClick: () -> Unit,
    dateFormatter: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (action != null) getActionIcon(action.type) else Icons.Default.Delete,
                contentDescription = null,
                tint = if (action != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action?.name ?: stringResource(R.string.deleted_activity),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = dateFormatter.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${String.format("%.1f", record.totalPoints)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (action != null) {
                    Text(
                        text = "${record.value} ${action.unit.toLocalizedString().lowercase()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}