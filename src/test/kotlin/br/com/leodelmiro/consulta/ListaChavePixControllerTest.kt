package br.com.leodelmiro.consulta

import br.com.leodelmiro.*
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListaChavePixControllerTest {

    @field:Inject
    lateinit var listaStub: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve listar chave quando for encontrada alguma chave do cliente`() {
        val idCliente = UUID.randomUUID()
        val idPix = UUID.randomUUID()
        val chaveAletoria = UUID.randomUUID().toString()
        val listaChavesRequest = listaChavesRequest(idCliente)
        val listaComChave = listaComChave(idPix, chaveAletoria)
        val listaChavesResponse = listaChavesResponse(idCliente, listaComChave)

        given(listaStub.listaChaves(listaChavesRequest)).willReturn(listaChavesResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$idCliente")
        val response = client.toBlocking().exchange(request, List::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
            assertEquals(body().size, 1)
        }

    }

    @Test
    fun `deve retornar lista vazia quando nao for encontrada nenhuma chave do cliente`() {
        val idCliente = UUID.randomUUID()
        val listaChavesRequest = listaChavesRequest(idCliente)
        val listaSemChave = emptyList<ListaChavesResponse.ChaveResponse>()
        val listaChavesResponse = listaChavesResponse(idCliente, listaSemChave)

        given(listaStub.listaChaves(listaChavesRequest)).willReturn(listaChavesResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$idCliente")
        val response = client.toBlocking().exchange(request, List::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
            assertTrue(body().isEmpty())
        }
    }

    private fun listaChavesRequest(idCliente: UUID): ListaChavesRequest {
        return ListaChavesRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .build()
    }

    private fun listaChavesResponse(
        idCliente: UUID,
        listaChaves: Iterable<ListaChavesResponse.ChaveResponse>
    ): ListaChavesResponse {
        return ListaChavesResponse.newBuilder()
            .setIdCliente(idCliente.toString())
            .addAllChavesPix(listaChaves)
            .build()
    }

    private fun listaComChave(
        idPix: UUID,
        chaveAletoria: String
    ) = MutableList(1) {
        ListaChavesResponse.ChaveResponse.newBuilder()
            .setIdPix(idPix.toString())
            .setTipoChave(TipoChave.ALEATORIA)
            .setChave(chaveAletoria)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadoEm(
                Timestamp.newBuilder()
                    .setSeconds(1618590168)
                    .setNanos(237044000)
                    .build()
            ).build()
    }

}

@Factory
@Replaces(factory = GrpcClientFactory::class)
internal class MockitoListaStubFactory {

    @Singleton
    fun stubMock() =
        Mockito.mock(KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub::class.java)
}