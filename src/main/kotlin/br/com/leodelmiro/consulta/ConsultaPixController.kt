package br.com.leodelmiro.consulta

import br.com.leodelmiro.ConsultaChaveRequest
import br.com.leodelmiro.KeyManagerConsultaGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}")
class ConsultaPixController(@Inject val gRpcClient: KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{idPix}")
    fun consultaPorId(@PathVariable idCliente: UUID, @PathVariable idPix: UUID): HttpResponse<Any> {

        logger.info("Consultando chave pix id: $idPix do cliente: $idCliente")

        val response = gRpcClient.consultaChave(
            ConsultaChaveRequest.newBuilder()
                .setPixEClienteId(
                    ConsultaChaveRequest.ConsultaPorPixEClienteId.newBuilder()
                        .setIdPix(idPix.toString())
                        .setIdCliente(idCliente.toString())
                ).build()
        )

        return HttpResponse.ok(DetalhesChavePixResponse(response))
    }
}