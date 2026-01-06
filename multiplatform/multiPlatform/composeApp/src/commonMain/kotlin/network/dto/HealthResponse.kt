package network.dto

import kotlinx.serialization.Serializable

/**
 * Health check response
 */
@Serializable
data class HealthResponse(
    val status: String,
    val message: String
)

