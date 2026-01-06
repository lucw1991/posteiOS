package ui.activities

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding

@Composable
fun VisibilityBadge(visibility: String) {
    when (visibility) {
        "public" -> Surface(
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = MaterialTheme.shapes.small
        ) { Text("public", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }

        "unlisted" -> Surface(
            color = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            shape = MaterialTheme.shapes.small
        ) { Text("unlisted", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }

        else -> Text("private", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
