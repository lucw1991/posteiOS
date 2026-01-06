package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for Post responses
 */
@Serializable
data class PostDto(
    val id: String,
    val folderId: String,
    val title: String,
    val url: String,
    val notes: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: String,
    val deletedAt: String? = null
)

