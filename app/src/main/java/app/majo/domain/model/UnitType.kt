package app.majo.domain.model

/**
 * Единицы измерения для активности.
 * Для бинарных типов обычно используется NONE.
 */
enum class UnitType {
    NONE,
    KM,
    METER,
    MINUTE,
    HOUR,
    REPETITION
}
