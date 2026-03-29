package app.majo.ui.screens.statistics

import app.majo.domain.model.action.Action
import app.majo.domain.model.action.ActionCategory

data class StatisticsState(
    val isLoading: Boolean = true,
    val totalPointsAllTime: Double = 0.0,
    val totalPointsThisMonth: Double = 0.0,
    val totalPointsThisWeek: Double = 0.0,
    val totalRecords: Int = 0,
    val topActions: List<TopAction> = emptyList(),
    val pointsByCategory: Map<ActionCategory, Double> = emptyMap()
)

data class TopAction(
    val action: Action,
    val totalPoints: Double
)