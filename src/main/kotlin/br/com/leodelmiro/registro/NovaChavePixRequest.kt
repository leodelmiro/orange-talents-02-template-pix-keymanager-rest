package br.com.leodelmiro.registro

import br.com.leodelmiro.RegistroChaveRequest
import br.com.leodelmiro.TipoChave
import br.com.leodelmiro.TipoConta
import br.com.leodelmiro.compartilhado.validacoes.ChavePixValida
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ChavePixValida
@Introspected
data class NovaChavePixRequest(@field:NotNull val tipoChave: TipoChaveRequest?,
                               @field:Size(max = 77) val chave: String?,
                               @field:NotNull val tipoConta: TipoContaRequest?
) {

    fun paraModeloGrpc(idCliente: UUID): RegistroChaveRequest {
        return RegistroChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setTipoChave(tipoChave?.atributoGrpc ?: TipoChave.CHAVE_DESCONHECIDA)
                .setChave(chave ?: "")
                .setTipoConta(tipoConta?.atributoGrpc ?: TipoConta.CONTA_DESCONHECIDA)
                .build()
    }

}

enum class TipoChaveRequest(val atributoGrpc: TipoChave) {
    CPF(TipoChave.CPF) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) return false

            val isCpfValido = CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }

            if (!chave.matches("^[0-9]{11}\$".toRegex()) || !isCpfValido) {
                return false
            }

            return true
        }

    },
    CELULAR(TipoChave.CELULAR) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank() || !chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                return false
            }

            return true
        }
    },
    EMAIL(TipoChave.EMAIL) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            val isEmailValido = EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }

            if (!isEmailValido) {
                return false
            }

            return true
        }

    },
    ALEATORIA(TipoChave.ALEATORIA) {
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }

    };

    abstract fun valida(chave: String?): Boolean
}

enum class TipoContaRequest(val atributoGrpc: TipoConta) {
    CONTA_CORRENTE(TipoConta.CONTA_CORRENTE), CONTA_POUPANCA(TipoConta.CONTA_POUPANCA)
}
