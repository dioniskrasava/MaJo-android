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
 * Card выглядит как "плитка" с:
 * - иконкой (слева)
 * - названием активности
 * - типом и единицей измерения
 * - количеством очков
 * - цветным бейджем категории (справа)
 *
 * Основная задача — компактно, аккуратно и понятно показать пользователю данные.
 *
 * @param action данные активности из доменной модели
 * @param onClick вызывается при нажатии на карточку
 *
 * Подходит для экрана списка активностей (ActionListScreen).
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        // Row = горизонтальный контейнер
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /**
             * Иконка слева.
             * Размер 32dp — хороший баланс между заметностью и компактностью.
             * Цвет = основной цвет темы.
             */
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = null, // нет смысла проговаривать вслух в скринридере
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            /**
             * Основной текстовый блок (название, тип, юнит, очки).
             * Column с weight(1f) занимает всё доступное пространство.
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
             * Бейдж категории (правее всего).
             * Например: FITNESS зелёный, HEALTH оранжевый.
             * Компактный цветной индикатор.
             */
            CategoryChip(action.category)
        }
    }
}

/**
 * Визуальный бейдж категории.
 * Состоит из:
 * - цветного фона (20% прозрачности)
 * - текста CATEGORY_NAME
 *
 * Такой элемент часто называют "Chip" (чип).
 */
@Composable
fun CategoryChip(category: ActionCategory) {

    // Выбор цвета для каждой категории.
    // В идеале вынести в отдельный файл theme.
    val color = when (category) {
        ActionCategory.FITNESS -> Color(0xFF4CAF50)    // зелёный
        ActionCategory.PRODUCTIVITY -> Color(0xFF2196F3) // синий
        ActionCategory.HEALTH -> Color(0xFFFF9800)     // оранжевый
        ActionCategory.EDUCATION -> Color(0xFF9C27B0)  // фиолетовый
        ActionCategory.OTHER -> Color(0xFF607D8B)      // серый
    }

    Box(
        modifier = Modifier
            // фон чипа слегка прозрачный → нежное оформление
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

/**
 * Функция подбирает подходящую иконку под тип активности.
 *
 * Это удобно:
 * - уменьшает дублирование кода
 * - делает использование в ActionCard проще
 * - визуально помогает пользователю быстро различать активности
 */
@Composable
fun getActionIcon(type: ActionType) = when (type) {

    // Для DISTANCE лучше всего подходит бегун
    ActionType.DISTANCE -> Icons.Filled.DirectionsRun

    // Время — часы
    ActionType.TIME     -> Icons.Filled.AccessTime

    // Повторы — гантеля лучше всего смотрится
    ActionType.COUNT    -> Icons.Filled.FitnessCenter

    // Бинарная активность (сделано/не сделано)
    ActionType.BINARY   -> Icons.Filled.Check
}
