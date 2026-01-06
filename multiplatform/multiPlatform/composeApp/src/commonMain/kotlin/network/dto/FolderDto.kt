package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for Folder responses
 */
@Serializable
data class FolderDto(
    val id: String,
    val ownerId: String,
    val title: String,
    val description: String? = null,
    val visibility: String,
    val createdAt: String,
    val deletedAt: String? = null
)

