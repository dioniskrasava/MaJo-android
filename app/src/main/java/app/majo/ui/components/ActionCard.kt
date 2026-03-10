package app.majo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.*
import app.majo.R
import app.majo.ui.theme.getColorByName
import app.majo.ui.util.toLocalizedString
import androidx.compose.material3.MaterialTheme
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Отображает карточку одной активности в списке.
 *
 * Это основной компонент для представления [Action] в списке активностей.
 * Card представляет собой "плитку" с:
 * - Иконкой, зависящей от типа активности.
 * - Названием, типом и количеством очков.
 * - Цветным бейджем категории ([CategoryChip]).
 *
 * @param action Данные активности из доменной модели.
 * @param onClick Лямбда-функция, вызываемая при нажатии на карточку (например, для перехода на экран деталей).
 */
@Composable
fun ActionCard(
    action: Action,
    useColors: Boolean,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLight = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
    val actionColor = if (useColors) getColorByName(action.color, isLight) else MaterialTheme.colorScheme.primary

    val backgroundColor = if (useColors) {
        actionColor.copy(alpha = 0.2f) // Полупрозрачный фон
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        //elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = action.name,
                tint = actionColor, // Иконка тоже цветная (или можно оставить primary)
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${action.type.toLocalizedString()} • ${action.unit.toLocalizedString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.points_per_unit, action.pointsPerUnit.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            CategoryChip(action.category)
        }
    }
}

// -------------------------------------------------------------------------------------------------

/**
 * Визуальный бейдж категории ("Chip").
 *
 * Отображает название категории, используя соответствующий цвет (Color) для фона
 * (с прозрачностью) и текста.
 *
 * @param category Категория [ActionCategory] для отображения.
 */
@Composable
fun CategoryChip(category: ActionCategory) {

    /**
     * Выбор цвета для каждой категории.
     * Здесь цвета жестко закодированы; в крупных проектах их выносят в Theme.
     */
    val color = when (category) {
        ActionCategory.FITNESS -> Color(0xFF4CAF50)    // зелёный
        ActionCategory.PRODUCTIVITY -> Color(0xFF2196F3) // синий
        ActionCategory.HEALTH -> Color(0xFFFF9800)     // оранжевый
        ActionCategory.EDUCATION -> Color(0xFF9C27B0)  // фиолетовый
        ActionCategory.OTHER -> Color(0xFF607D8B)      // серый
    }

    Box(
        modifier = Modifier
            // фон чипа слегка прозрачный (alpha = 0.2f)
            .background(color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = category.toLocalizedString(),
            color = color,
            style = MaterialTheme.typography.labelSmall // Небольшой, но заметный шрифт
        )
    }
}

// -------------------------------------------------------------------------------------------------

/**
 * Вспомогательная Composable-функция для подбора иконки по типу активности.
 *
 * Это удобно: уменьшает дублирование кода и делает ActionCard более чистым.
 *
 * @param type Тип активности [ActionType].
 * @return [ImageVector] — векторная иконка Material Design.
 */
@Composable
fun getActionIcon(type: ActionType) = when (type) {

    // Для DISTANCE лучше всего подходит иконка бегуна
    ActionType.DISTANCE -> Icons.Filled.DirectionsRun

    // Время — часы
    ActionType.TIME     -> Icons.Filled.AccessTime

    // Повторы/Количество — гантеля
    ActionType.COUNT    -> Icons.Filled.FitnessCenter

    // Бинарная активность (сделано/не сделано) — галочка
    ActionType.BINARY   -> Icons.Filled.Check
}