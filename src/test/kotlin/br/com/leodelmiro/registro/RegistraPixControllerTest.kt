package br.com.leodelmiro.registro

import br.com.leodelmiro.GrpcClientFactory
import br.com.leodelmiro.KeyManagerRegistraGrpcServiceGrpc
import br.com.leodelmiro.RegistroChaveResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraPixControllerTest() {

    @field:Inject
    lateinit var registraStub: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve registrar chave quando tudo estiver Ok`() {
        val chavePix = UUID.randomUUID().toString()
        val idPix = UUID.randomUUID().toString()
        val idCliente = UUID.randomUUID().toString()

        val respostaGrpc = grpcResponse(chavePix, idPix)

        given(registraStub.registrarChave(Mockito.any())).willReturn(respostaGrpc)

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste@teste.com.br",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val response = client.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(idPix))
    }


    @Test
    fun `deve retornar 422 quando chave já existir`() {
        val idCliente = UUID.randomUUID().toString()

        given(registraStub.registrarChave(Mockito.any()))
            .willThrow(StatusRuntimeException(Status.ALREADY_EXISTS))

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste@teste.com.br",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val thrown = assertThrows<HttpClientResponseException> {
            (client.toBlocking().exchange(request, NovaChavePixRequest::class.java))
        }

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.status)
    }

    @Test
    fun `deve retornar 400 quando for passado algum dado invalido`() {
        val idCliente = UUID.randomUUID().toString()

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val thrown = assertThrows<HttpClientResponseException> {
            (client.toBlocking().exchange(request, NovaChavePixRequest::class.java))
        }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }
}

private fun novaChavePixRequest(
    tipoChave: TipoChaveRequest,
    chave: String?,
    tipoContaRequest: TipoContaRequest
): NovaChavePixRequest? {
    return NovaChavePixRequest(tipoChave, chave, tipoContaRequest)
}

private fun grpcResponse(chavePix: String, idPix: String): RegistroChaveResponse? {
    return RegistroChaveResponse.newBuilder()
        .setChavePix(chavePix)
        .setIdPix(idPix)
        .build()
}

@Factory
@Replaces(factory = GrpcClientFactory::class)
internal class MockitoStubFactory {

    @Singleton
    fun stubMock() =
        Mockito.mock(KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub::class.java)

}


