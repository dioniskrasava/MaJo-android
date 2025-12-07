package app.majo.domain.repository


import app.majo.domain.model.action.ActionRecord
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления записями активностей ([ActionRecord]).
 *
 * Этот интерфейс является **контрактом** (Contract) для слоя данных,
 * определяющим все операции чтения и записи, связанные с фактическим
 * выполнением активностей пользователем.
 *
 * Он позволяет бизнес-логике запрашивать записи:
 * 1. По конкретной активности (для истории).
 * 2. За определенный период (для статистики).
 */
interface RecordRepository {

    /**
     * Возвращает реактивный поток всех записей для конкретной активности.
     *
     * Поток автоматически испускает новый список записей при их изменении.
     *
     * @param activityId Уникальный Long-идентификатор связанной активности [Action].
     * @return [Flow] списка доменных моделей [ActionRecord].
     */
    fun getRecordsForActivity(activityId: Long): Flow<List<ActionRecord>>

    /**
     * Возвращает реактивный поток записей, созданных в заданном временном диапазоне.
     *
     * Этот метод критически важен для расчета сводной статистики ([PeriodSummary]).
     *
     * @param start Временная метка начала периода (в миллисекундах).
     * @param end Временная метка окончания периода (в миллисекундах).
     * @return [Flow] списка доменных моделей [ActionRecord].
     */
    fun getRecordsForPeriod(start: Long, end: Long): Flow<List<ActionRecord>>

    /**
     * Добавляет новую запись о выполнении активности.
     *
     * Является suspend-функцией, что указывает на выполнение в фоновом потоке.
     *
     * @param record Доменная модель [ActionRecord] для вставки.
     */
    suspend fun insert(record: ActionRecord)

    /**
     * Удаляет запись о выполнении активности по ее ID.
     *
     * @param id ID записи [ActionRecord], которую нужно удалить.
     */
    suspend fun delete(id: Long)
}