package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for creating a new post
 */
@Serializable
data class PostCreateDto(
    val folderId: String,
    val title: String,
    val url: String,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

