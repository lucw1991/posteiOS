package ui.activities.appBar_searchBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import ui.activities.homePage.SearchState
import ui.activities.homePage.SearchTarget
import ui.activities.homePage.ResultFolderRow
import ui.activities.homePage.ResultPostRow
import kotlin.math.roundToInt

@Composable
fun SearchControls(searchState: SearchState,
                           onQueryChange: (String) -> Unit,
                           onTargetChange: (SearchTarget) -> Unit) {

    // Track text field position and size to anchor the overlay below it
    var fieldPos by remember { mutableStateOf(Offset.Zero) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }

    // For toggle colors
    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White,
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        labelColor = Color.Black,
        selectedLabelColor = Color.Black,
        selectedLeadingIconColor = Color.Black
    )

    // For Toggle borders. I figure this will keep it more visible but this can be tweaked for looks
    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = Color.Black,
        selectedBorderColor = Color.Black,
        disabledBorderColor = Color.Black,
        borderWidth = 1.dp,
        selectedBorderWidth = 1.dp
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Nudge towards the middle
        Spacer(Modifier.height(16.dp))

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            OutlinedTextField(
                value = searchState.query, onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)
                    .onGloballyPositioned { coords ->
                        fieldPos = coords.positionInWindow()
                        fieldSize = coords.size
                    },
                placeholder = { Text("Search posts or folders") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                },
                singleLine = true, textStyle = TextStyle(fontSize = 14.sp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.DarkGray
                )
            )
        }

        // Centered chip toggles for Posts and Folders
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            FilterChip(
                selected = searchState.target == SearchTarget.POSTS,
                onClick = { onTargetChange(SearchTarget.POSTS) },
                label = { Text("Posts") },
                modifier = Modifier.padding(end = 6.dp),
                colors = chipColors,
                border = chipBorder
            )
            FilterChip(
                selected = searchState.target == SearchTarget.FOLDERS,
                onClick = { onTargetChange(SearchTarget.FOLDERS) },
                label = { Text("Folders") },
                modifier = Modifier.padding(end = 6.dp),
                colors = chipColors,
                border = chipBorder
            )
        }

        // Floating overlay for results
        if (searchState.query.isNotBlank()) {
            // Place pop up just below the search bar
            val offsetY = fieldPos.y.roundToInt() + fieldSize.height
            val offsetX = fieldPos.x.roundToInt()
            Popup(
                offset = IntOffset(offsetX, offsetY),
                properties = PopupProperties(focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true),
                /* Clear out text to close results. If we don't like this bit in app
                   remove it here. */
                onDismissRequest = { onQueryChange("") }
            ) {
                Surface(
                    tonalElevation = 6.dp, shadowElevation = 6.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 80.dp, max = 320.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    when {
                        searchState.loading -> {
                            Row(Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        searchState.error != null -> {
                            Text(
                                text = "Search error: ${searchState.error}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        searchState.target == SearchTarget.POSTS -> {
                            if (searchState.posts.isEmpty()) {
                                Text(
                                    text = "No matching post for \"${searchState.query}\"",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(Modifier.padding(vertical = 8.dp)) {
                                    searchState.posts.forEachIndexed { i, post ->
                                        ResultPostRow(post)
                                        if (i < searchState.posts.lastIndex) {
                                            HorizontalDivider(
                                                Modifier,
                                                DividerDefaults.Thickness,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            if (searchState.folders.isEmpty()) {
                                Text(
                                    text = "No matching folders for \"${searchState.query}\"",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(Modifier.padding(vertical = 8.dp)) {
                                    searchState.folders.forEachIndexed { i, folder ->
                                        ResultFolderRow(folder)
                                        if (i < searchState.folders.lastIndex) {
                                            HorizontalDivider(
                                                Modifier,
                                                DividerDefaults.Thickness,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}