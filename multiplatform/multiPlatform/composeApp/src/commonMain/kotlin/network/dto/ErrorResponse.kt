package network.dto

import kotlinx.serialization.Serializable

/**
 * Standard error response
 */
@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val details: List<ValidationDetail>? = null
) {
    @Serializable
    data class ValidationDetail(
        val field: String,
        val reason: String
    )
}

