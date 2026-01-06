package network.exception

/**
 * Base exception for API errors
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when a resource is not found (404)
 */
class NotFoundException(message: String) : ApiException(message)

/**
 * Exception thrown when access is forbidden (403)
 */
class ForbiddenException(message: String) : ApiException(message)

/**
 * Exception thrown when request is unauthorized (401)
 */
class UnauthorizedException(message: String) : ApiException(message)

/**
 * Exception thrown when request validation fails (400)
 */
class BadRequestException(message: String, val details: List<network.dto.ErrorResponse.ValidationDetail>? = null) : ApiException(message)

/**
 * Exception thrown when server error occurs (5xx)
 */
class ServerException(message: String, cause: Throwable? = null) : ApiException(message, cause)

/**
 * Exception thrown for network-related errors
 */
class NetworkException(message: String, cause: Throwable? = null) : ApiException(message, cause)

/**
 * Exception thrown for unknown errors
 */
class UnknownApiException(message: String, cause: Throwable? = null) : ApiException(message, cause)

