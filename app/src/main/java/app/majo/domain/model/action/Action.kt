package app.majo.domain.model.action

/**
 * Модель отслеживаемой активности.
 *
 * Это **доменная модель** (Domain Model) — центральное понятие в слое бизнес-логики.
 * Она содержит все атрибуты, необходимые для работы с активностью
 * в коде приложения (ViewModel, Use Cases), и полностью независима
 * от формата хранения данных (ActionEntity).
 *
 * Примеры:
 * - Бег
 * - Медитация
 * - Программирование
 * - Тренировка
 *
 * Каждая активность определяет:
 * - в каких единицах измеряется выполнение
 * - сколько очков начисляется за единицу
 *
 * @property id Уникальный идентификатор активности (Primary Key).
 * @property name Название активности, отображаемое пользователю.
 * @property type Тип активности, используется для классификации и логики (например, [ActionType.TIME]).
 * @property unit Единица измерения, в которой ведется трекинг (например, [UnitType.MINUTE], [UnitType.KM]).
 * @property pointsPerUnit Количество очков, которое пользователь получает за каждую единицу [unit].
 * @property category Категория активности. Значение по умолчанию: [ActionCategory.OTHER].
 * @property isActive Флаг доступности. Значение по умолчанию: true.
 * @property createdAt Временная метка создания активности в миллисекундах. Значение по умолчанию: текущее время.
 */
data class Action(
    val id: Long,
    val name: String,
    val type: ActionType,
    val unit: UnitType,
    val pointsPerUnit: Double,
    val category: ActionCategory = ActionCategory.OTHER,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)