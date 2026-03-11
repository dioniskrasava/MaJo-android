package app.majo.domain.model.action

import kotlinx.serialization.Serializable

@Serializable
enum class ActionCategory {
    FITNESS,
    PRODUCTIVITY,
    HEALTH,
    EDUCATION,
    OTHER
}