package app.majo.domain.model

/**
 * Пользовательские настройки приложения.
 *
 * Эта доменная модель инкапсулирует все ключевые настройки, которые
 * может изменить пользователь, и представляет собой **единый контракт**
 * для работы с пользовательскими предпочтениями в приложении.
 *
 * Как доменная модель, она независима от способа хранения (например, DataStore или SharedPreferences).
 *
 * @property darkTheme Флаг, указывающий, включена ли темная тема. По умолчанию: false (светлая тема).
 * @property notificationsEnabled Флаг, указывающий, разрешены ли уведомления от приложения. По умолчанию: true.
 */
data class UserSettings(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true
)