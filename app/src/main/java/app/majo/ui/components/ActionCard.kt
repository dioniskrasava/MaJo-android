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
import androidx.compose.ui.tooling.preview.Preview
import app.majo.ui.theme.PurpleDark
import app.majo.ui.theme.PurpleLight

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
    cardAlpha: Float,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLight = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
    val actionColor = if (useColors) getColorByName(action.color, isLight) else MaterialTheme.colorScheme.primary

    val backgroundColor = if (useColors) {
        actionColor.copy(alpha = cardAlpha) // Полупрозрачный фон
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = action.name,
                tint = MaterialTheme.colorScheme.primary, // Иконка тоже цветная (или можно оставить primary)
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row{
                    Text(
                        text = action.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "${action.type.toLocalizedString()} • ${action.unit.toLocalizedString()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
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


// ТЕСТИРУЕМ ВИЗУАЛ КАРТОЧКИ ЧЕРЕЗ ПРЕВЬЮ

// ===== Превью =====

private fun previewAction(
    name: String = "Пробежка",
    type: ActionType = ActionType.DISTANCE,
    unit: UnitType = UnitType.KM,
    pointsPerUnit: Double = 2.5,
    category: ActionCategory = ActionCategory.FITNESS,
    color: String = "Purple"
) = Action(
    id = 1L,
    name = name,
    type = type,
    unit = unit,
    pointsPerUnit = pointsPerUnit,
    category = category,
    isActive = true,
    createdAt = 0L,
    color = color,
    ticker = name.take(3).uppercase()
)

@Composable
private fun PreviewTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PurpleLight,
        content = content
    )
}

@Preview(name = "Light", showBackground = true)
@Composable
fun ActionCardPreviewLight() {
    PreviewTheme() {
        ActionCard(previewAction(), useColors = true, cardAlpha = 0.4f, onClick = {})
    }
}
