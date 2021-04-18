package br.com.leodelmiro.consulta

import br.com.leodelmiro.KeyManagerListaGrpcServiceGrpc
import br.com.leodelmiro.ListaChavesRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}")
class ListaChavePixControllerclass(@Inject val gRpcClient: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get
    fun listaChavePix(@PathVariable idCliente: UUID): HttpResponse<Any> {
        logger.info("Consultando chaves do cliente: $idCliente")

        val response = gRpcClient.listaChaves(
            ListaChavesRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .build()
        )

        val chaves = response.chavesPixList.map { ChavePixResponse(it) }
        return HttpResponse.ok(chaves)
    }
}