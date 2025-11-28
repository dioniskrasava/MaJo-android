package app.majo.domain.model

/**
 * Тип отслеживаемой активности.
 *
 * DISTANCE — расстояние (км, метры и т.п.)
 * TIME — время (минуты, часы)
 * COUNT — количество повторений или единиц
 * BINARY — бинарная активность (сделано / не сделано)
 */
enum class ActionType {
    DISTANCE,
    TIME,
    COUNT,
    BINARY
}
