package app.majo.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.majo.R
import app.majo.domain.model.action.ActionCategory
import app.majo.domain.model.action.ActionType
import app.majo.domain.model.action.UnitType

@Composable
fun ActionType.toLocalizedString(): String = when (this) {
    ActionType.DISTANCE -> stringResource(R.string.action_type_distance)
    ActionType.TIME     -> stringResource(R.string.action_type_time)
    ActionType.COUNT    -> stringResource(R.string.action_type_count)
    ActionType.BINARY   -> stringResource(R.string.action_type_binary)
}

@Composable
fun ActionCategory.toLocalizedString(): String = when (this) {
    ActionCategory.FITNESS      -> stringResource(R.string.action_category_fitness)
    ActionCategory.PRODUCTIVITY -> stringResource(R.string.action_category_productivity)
    ActionCategory.HEALTH       -> stringResource(R.string.action_category_health)
    ActionCategory.EDUCATION    -> stringResource(R.string.action_category_education)
    ActionCategory.OTHER        -> stringResource(R.string.action_category_other)
}

@Composable
fun UnitType.toLocalizedString(): String = when (this) {
    UnitType.NONE        -> stringResource(R.string.unit_type_none)
    UnitType.KM          -> stringResource(R.string.unit_type_km)
    UnitType.METER       -> stringResource(R.string.unit_type_meter)
    UnitType.MINUTE      -> stringResource(R.string.unit_type_minute)
    UnitType.HOUR        -> stringResource(R.string.unit_type_hour)
    UnitType.REPETITION  -> stringResource(R.string.unit_type_repetition)
}