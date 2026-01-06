package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for creating a new folder
 */
@Serializable
data class FolderCreateDto(
    val title: String,
    val description: String? = null,
    val visibility: String = "private"
)

