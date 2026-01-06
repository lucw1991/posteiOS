package ui.activities.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import model.Folder
import model.Post
import network.PosteAPIClient



@Composable
fun ResultPostRow(post: Post) {
    Surface(tonalElevation = 1.dp, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(12.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            post.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ResultFolderRow(folder: Folder) {
    Surface(tonalElevation = 1.dp, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(12.dp)) {
            Text(folder.title, style = MaterialTheme.typography.titleMedium)
            folder.description?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

// For preview row scrolling
@Composable
fun <T> AutoScrollRow(
    items: List<T>,
    speed: Dp = 24.dp,
    reverse: Boolean = false,  // True = left to right, False = right to left
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    itemSpacing: Dp = 8.dp, itemContent: @Composable (T) -> Unit
) {

    if (items.isEmpty()) {
        return
    }

    // Renders 3 copies so they can wrap without a visible jump
    val data = remember(items) {
        items + items + items
    }
    val middleStartIndex = items.size  // Starts on middle copy

    val state = rememberLazyListState()
    val pxPerSec = with(LocalDensity.current) {
        speed.toPx()
    } * (if (reverse) -1 else 1)

    LaunchedEffect(items, pxPerSec) {
        state.scrollToItem(middleStartIndex, 0)
        var last = withFrameNanos { it }
        while (isActive) {
            val now = withFrameNanos { it }
            val dt = (now - last) / 1_000_000_000f
            last = now
            state.scrollBy(pxPerSec * dt)
            // This should make the wrap seamless. Hovers around middle copy.
            val i = state.firstVisibleItemIndex
            if ( i <= 0 || i >= items.size * 2) {
                state.scrollToItem(middleStartIndex, state.firstVisibleItemScrollOffset)
            }
            yield()
        }
    }

    LazyRow(
        state = state,
        userScrollEnabled = false,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        itemsIndexed(data, key = {
                idx, _ -> idx
        }) {
                _, item ->
            itemContent(item)
        }
    }

}