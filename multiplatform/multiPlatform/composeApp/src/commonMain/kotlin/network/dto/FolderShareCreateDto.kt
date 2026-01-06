package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for creating a folder share
 */
@Serializable
data class FolderShareCreateDto(
    val userEmail: String,
    val permission: String = "view"
)

