package app.majo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Check

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.*





@Composable
fun ActionCard(
    action: Action,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- Иконка слева ---
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // --- Текстовая часть ---
            Column(modifier = Modifier.weight(1f)) {

                // Название
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Тип и юнит
                Text(
                    text = "${action.type.name} • ${action.unit.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Очки
                Text(
                    text = "Очков за единицу: ${action.pointsPerUnit}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // --- Категория справа как бейдж ---
            CategoryChip(action.category)
        }
    }
}

@Composable
fun CategoryChip(category: ActionCategory) {
    val color = when (category) {
        ActionCategory.FITNESS -> Color(0xFF4CAF50)
        ActionCategory.PRODUCTIVITY -> Color(0xFF2196F3)
        ActionCategory.HEALTH -> Color(0xFFFF9800)
        ActionCategory.EDUCATION -> Color(0xFF9C27B0)
        ActionCategory.OTHER -> Color(0xFF607D8B)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = category.name,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun getActionIcon(type: ActionType) = when (type) {
    ActionType.DISTANCE -> Icons.Filled.DirectionsRun
    ActionType.TIME     -> Icons.Filled.AccessTime
    ActionType.COUNT    -> Icons.Filled.FitnessCenter
    ActionType.BINARY   -> Icons.Filled.Check
}
