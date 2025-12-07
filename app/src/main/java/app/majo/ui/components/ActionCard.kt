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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.majo.domain.model.action.*

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
    onClick: () -> Unit
) {

    // Card = визуальный контейнер с фоном, скруглениями и тенью.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // небольшие внешние отступы между карточками
            .padding(horizontal = 12.dp, vertical = 6.dp)
            // позволяет нажимать на карточку
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            // MaterialTheme автоматически подбирает цвета под тему (светлую/тёмную)
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Использует слегка отличающийся от фона цвет
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        // Row = горизонтальный контейнер, выравнивает все элементы по вертикали (CenterVertically)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /**
             * Иконка слева. Выбирается с помощью вспомогательной функции [getActionIcon].
             */
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = action.name, // Используем название как описание для скринридера
                tint = MaterialTheme.colorScheme.primary, // Основной акцентный цвет
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            /**
             * Основной текстовый блок. [Column] с [Modifier.weight(1f)] заставляет его
             * занять всё доступное пространство между иконкой и чипом.
             */
            Column(modifier = Modifier.weight(1f)) {

                // Название активности
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Тип и юнит (например: DISTANCE • KM)
                Text(
                    text = "${action.type.name} • ${action.unit.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Очки за единицу
                Text(
                    text = "Очков за единицу: ${action.pointsPerUnit}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            /**
             * Бейдж категории (правее всего), созданный отдельным композаблом.
             */
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
            text = category.name,
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