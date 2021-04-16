package br.com.leodelmiro.consulta

import br.com.leodelmiro.ConsultaChaveResponse
import br.com.leodelmiro.TipoChave
import br.com.leodelmiro.TipoConta
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DetalhesChavePixResponse(chaveResponse: ConsultaChaveResponse) {

    val idPix: String = chaveResponse.idPix
    val idCliente: String = chaveResponse.idClient
    val chaveInfo = ChavePixInfoResponse(chaveResponse.chave)
}

class ChavePixInfoResponse(chaveInfo: ConsultaChaveResponse.ChavePixInfo) {
    val tipoChave = when (chaveInfo.tipoChave) {
        TipoChave.ALEATORIA -> "ALEATORIA"
        TipoChave.EMAIL -> "EMAIL"
        TipoChave.CELULAR -> "CELULAR"
        TipoChave.CPF -> "CPF"
        else -> "DESCONHECIDA"
    }

    val chavePix: String = chaveInfo.chavePix
    val conta = ContaResponse(chaveInfo.conta)

    val criadoEm: LocalDateTime = chaveInfo.criadoEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}

class ContaResponse(conta: ConsultaChaveResponse.ChavePixInfo.Conta) {
    val instituicao: String = conta.instituicao
    val nomeTitular: String = conta.nomeTitular
    val cpfTitular: String = conta.cpfTitular

    val tipoConta = when(conta.tipoConta) {
        TipoConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        TipoConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        else -> "DESCONHECIDA"
    }

    val agencia: String = conta.agencia
    val numero: String = conta.numero
}
