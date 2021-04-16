package br.com.leodelmiro.compartilhado

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class GlobalExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<*>> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handle(request: HttpRequest<*>, exception: StatusRuntimeException): HttpResponse<*> {

        val statusCode = exception.status.code
        val statusDescriptions = exception.status.description

        val (httpStatus, message) = when (statusCode) {
            Status.NOT_FOUND.code -> Pair(HttpStatus.NOT_FOUND, statusDescriptions)
            Status.INVALID_ARGUMENT.code -> Pair(HttpStatus.BAD_REQUEST, statusDescriptions)
            Status.ALREADY_EXISTS.code -> Pair(HttpStatus.UNPROCESSABLE_ENTITY, statusDescriptions)
            else -> {
                logger.error("Erro inesperado ${exception.javaClass.name} ao processar requisição", exception)
                Pair(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível completar requisição")
            }
        }

        return HttpResponse.status<JsonError>(httpStatus).body(JsonError(message))
    }
}