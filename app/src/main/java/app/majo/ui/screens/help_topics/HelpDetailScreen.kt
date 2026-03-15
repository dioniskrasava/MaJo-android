package app.majo.ui.screens.help_topics


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.majo.R
import app.majo.ui.common.SimpleTopAppBar


@Composable
fun HelpDetailScreen(
    topicId: String,
    onNavigateBack: () -> Unit
) {
    val (titleRes, contentRes, imageRes) = when (topicId) {
        "what_is_activity" -> Triple(
            R.string.help_topic_activity,
            R.string.help_content_activity,
            null//R.drawable.help_activity   // опционально ????
        )
        "what_is_record" -> Triple(
            R.string.help_topic_record,
            R.string.help_content_record,
            null//R.drawable.help_record
        )
        "about_points" -> Triple(
            R.string.help_topic_points,
            R.string.help_content_points,
            null//R.drawable.help_points
        )
        else -> Triple(R.string.help, R.string.help_content_default, null)
    }

    Scaffold(
        topBar = { SimpleTopAppBar(title = stringResource(titleRes), onNavigateBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                text = stringResource(contentRes),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}