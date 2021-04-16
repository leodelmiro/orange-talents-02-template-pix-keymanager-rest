package br.com.leodelmiro.consulta

import br.com.leodelmiro.*
import com.google.protobuf.Timestamp
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultaPixControllerTest {

    @field:Inject
    lateinit var consultaStub: KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve consultar chave quando tudo estiver Ok`() {
        val idPix = UUID.randomUUID()
        val idCliente = UUID.randomUUID()
        val consultaChaveResponse = grpcResponse(idPix.toString(), idCliente.toString())

        given(consultaStub.consultaChave(Mockito.any())).willReturn(consultaChaveResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val response = client.toBlocking().exchange(request, Any::class.java)


        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body)
        }
    }

    @Test
    fun `deve retornar 404 quando chave nao for encontrada`() {
        val idPix = UUID.randomUUID()
        val idCliente = UUID.randomUUID()

        given(consultaStub.consultaChave(Mockito.any()))
            .willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }

    @Test
    fun `deve retornar 400 quando cliente id tiver formato invalido`() {
        val idPix = UUID.randomUUID()
        val idCliente = 123

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }

    private fun grpcResponse(chavePix: String, idPix: String): ConsultaChaveResponse? {
        return ConsultaChaveResponse.newBuilder()
            .setIdPix(chavePix)
            .setIdClient(idPix)
            .setChave(
                ConsultaChaveResponse.ChavePixInfo.newBuilder()
                    .setTipoChave(TipoChave.ALEATORIA)
                    .setChavePix("bfb23aeb-f7ed-43ae-9885-479eb861f994")
                    .setConta(
                        ConsultaChaveResponse.ChavePixInfo.Conta.newBuilder()
                            .setInstituicao("ITAÚ UNIBANCO S.A.")
                            .setNomeTitular("Leonardo Delmiro")
                            .setCpfTitular("02467781054")
                            .setTipoConta(TipoConta.CONTA_CORRENTE)
                            .setAgencia("0001")
                            .setNumero("291900")
                            .build()
                    )
                    .setCriadoEm(
                        Timestamp.newBuilder()
                            .setSeconds(1618590168)
                            .setNanos(237044000)
                            .build()
                    )
                    .build()
            )
            .build()
    }
}

@Factory
@Replaces(factory = GrpcClientFactory::class)
internal class MockitoStubFactory {

    @Singleton
    fun stubMock() =
        Mockito.mock(KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub::class.java)

}