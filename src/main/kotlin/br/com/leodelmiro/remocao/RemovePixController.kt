package br.com.leodelmiro.remocao

import br.com.leodelmiro.KeyManagerRemoveGrpcServiceGrpc
import br.com.leodelmiro.RemocaoChaveRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}/pix/{idPix}")
class RemovePixController(@Inject val gRpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Delete
    fun remove(@PathVariable idCliente: UUID, @PathVariable idPix: UUID): HttpResponse<Any> {
        logger.info("Removendo chave pix id: $idPix do cliente: $idCliente")

        gRpcClient.removerChave(
            RemocaoChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setIdPix(idPix.toString())
                .build()
        )

        return HttpResponse.ok()
    }
}