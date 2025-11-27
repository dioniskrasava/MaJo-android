package app.majo.domain.model

/**
 * Пользовательские настройки приложения.
 */
data class UserSettings(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true
)
