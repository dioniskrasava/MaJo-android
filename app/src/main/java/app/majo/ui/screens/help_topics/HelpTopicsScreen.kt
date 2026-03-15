package app.majo.ui.screens.help_topics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.majo.R
import app.majo.ui.common.SimpleTopAppBar

// HelpTopicsScreen.kt
@Composable
fun HelpTopicsScreen(
    onNavigateBack: () -> Unit,
    onTopicClick: (String) -> Unit // передаём идентификатор темы
) {
    val topics = listOf(
        "what_is_activity" to R.string.help_topic_activity,
        "what_is_record" to R.string.help_topic_record,
        "about_points" to R.string.help_topic_points
    )

    Scaffold(
        topBar = { SimpleTopAppBar(title = stringResource(R.string.help), onNavigateBack = onNavigateBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics) { (id, titleRes) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTopicClick(id) },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = stringResource(titleRes),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}