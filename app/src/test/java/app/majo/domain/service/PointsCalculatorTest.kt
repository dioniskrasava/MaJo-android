package app.majo.domain.service

import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType
import org.junit.Assert.assertEquals
import org.junit.Test

class PointsCalculatorTest {

    // 1. Тест для стандартного типа (DISTANCE, TIME, COUNT)
    @Test
    fun calculatePoints_standardType_shouldMultiplyValueAndPoints() {
        // ARRANGE (Подготовка)
        // Создаем "Активность" типа DISTANCE, где 1 единица = 10.0 очков.
        val testAction = Action(
            id = 1,
            name = "Бег",
            type = ActionType.DISTANCE, // Тип, где идет умножение
            unit = UnitType.KM,
            pointsPerUnit = 10.0,
            category = app.majo.domain.model.action.ActionCategory.FITNESS
        )
        val testValue = 5.0 // Пробежали 5 км

        // ACT (Действие)
        // Вызываем функцию с нашими входными данными
        val actualPoints = PointsCalculator.calculatePoints(testAction, testValue)

        // ASSERT (Проверка)
        // Ожидаемый результат: 5.0 * 10.0 = 50.0
        val expectedPoints = 50.0

        // Сравниваем ожидаемое и фактическое.
        // Третий параметр (0.001) - это "дельта" для сравнения чисел с плавающей точкой (Double).
        assertEquals(expectedPoints, actualPoints, 0.001)
    }
}