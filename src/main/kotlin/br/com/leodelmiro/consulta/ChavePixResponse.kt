package br.com.leodelmiro.consulta

import br.com.leodelmiro.ListaChavesResponse
import br.com.leodelmiro.TipoChave
import br.com.leodelmiro.TipoConta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChavePixResponse(chaveResponse: ListaChavesResponse.ChaveResponse) {

    val id = chaveResponse.idPix

    val tipoChave = when (chaveResponse.tipoChave) {
        TipoChave.ALEATORIA -> "ALEATORIA"
        TipoChave.EMAIL -> "EMAIL"
        TipoChave.CELULAR -> "CELULAR"
        TipoChave.CPF -> "CPF"
        else -> "DESCONHECIDA"
    }

    val chave = chaveResponse.chave

    val tipoConta = when (chaveResponse.tipoConta) {
        TipoConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        TipoConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        else -> "DESCONHECIDA"
    }

    val criadoEm: LocalDateTime = chaveResponse.criadoEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}