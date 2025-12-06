package app.majo.data.repository

import app.majo.data.local.dao.ActionDao
import app.majo.data.local.mappers.toDomain
import app.majo.data.local.mappers.toEntity
import app.majo.domain.model.action.Action
import app.majo.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Конкретная реализация интерфейса [ActionRepository].
 *
 * Этот класс является **единственным источником истины (Single Source of Truth)**
 * для данных, связанных с активностями. Он изолирует бизнес-логику (ViewModel)
 * от деталей реализации базы данных (Room/DAO).
 *
 * @property dao Объект доступа к данным [ActionDao], через который
 * осуществляется фактическое взаимодействие с локальной базой данных.
 */
class ActionRepositoryImpl(
    private val dao: ActionDao
) : ActionRepository {

    /**
     * Получает реактивный поток всех активностей из локальной базы данных.
     *
     * Используется оператор **[Flow.map]** для преобразования реактивного потока
     * данных из формата базы данных ([ActionEntity]) в формат доменной модели ([Action]),
     * используемый бизнес-логикой. Это критически важно для архитектуры.
     *
     * @return [Flow] списка доменных моделей [Action].
     */
    override fun getActions(): Flow<List<Action>> =
        dao.getActions().map { list ->
            // Применяем функцию toDomain() к каждому элементу списка
            list.map { it.toDomain() }
        }

    /**
     * Получает одну доменную модель [Action] по ее ID.
     *
     * Сначала извлекает сущность [ActionEntity] из DAO, затем преобразует ее
     * в доменную модель [Action] с помощью функции-расширения `toDomain()`.
     *
     * @param id ID активности.
     * @return [Action] или null, если активность не найдена.
     */
    override suspend fun getActionById(id: Long): Action? =
        dao.getById(id)?.toDomain()

    /**
     * Вставляет новую доменную модель [Action] в базу данных.
     *
     * Сначала преобразует доменную модель [Action] в [ActionEntity]
     * с помощью `toEntity()`, а затем делегирует операцию вставки DAO.
     *
     * @param action Доменная модель [Action] для вставки.
     */
    override suspend fun insert(action: Action) {
        dao.insert(action.toEntity())
    }

    /**
     * Обновляет существующую доменную модель [Action] в базе данных.
     *
     * Необходимое преобразование [Action] в [ActionEntity] выполняется
     * с помощью маппера.
     *
     * @param action Доменная модель [Action] для обновления.
     */
    override suspend fun update(action: Action) {
        dao.update(action.toEntity())
    }

    /**
     * Удаляет активность из базы данных по ее ID.
     *
     * Операция делегируется непосредственно [ActionDao].
     *
     * @param id ID активности для удаления.
     */
    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}