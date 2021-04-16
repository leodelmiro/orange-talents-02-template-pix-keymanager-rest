package br.com.leodelmiro.registro

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class TipoChaveRequestTest {

    @Nested
    inner class Cpf {

        @ParameterizedTest
        @CsvSource("118.421.000-41", "12345678901", ",", "''")
        fun `valida do tipo CPF deve retornar false quando for cpf invalido`(cpf: String?) {

            val result = TipoChaveRequest.CPF.valida(cpf)

            assertFalse(result)
        }

        @Test
        fun `valida do tipo CPF deve retornar true quando for cpf valido`() {
            val result = TipoChaveRequest.CPF.valida("11842100041")

            assertTrue(result)
        }
    }

    @Nested
    inner class Celular {

        @ParameterizedTest
        @CsvSource("12345678", "+ddddddddddedes", "+55(85)98871-4077", ",", "''")
        fun `valida do tipo Celular deve retornar false quando for celular invalido`(celular: String?) {

            val result = TipoChaveRequest.CELULAR.valida(celular)

            assertFalse(result)
        }

        @Test
        fun `valida do tipo Celular deve retornar true quando for celular valido`() {
            val result = TipoChaveRequest.CELULAR.valida("+5585988714077")

            assertTrue(result)
        }
    }

    @Nested
    inner class Email {
        @ParameterizedTest
        @CsvSource(
            "email.email.com",
            "teste@",
            ",",
            "''"
        )
        fun `valida do tipo Email deve retornar false quando for email invalido`(email: String?) {

            val result = TipoChaveRequest.EMAIL.valida(email)

            assertFalse(result)
        }

        @Test
        fun `valida do tipo Email deve retornar true quando for email valido`() {
            val result = TipoChaveRequest.EMAIL.valida("email@email.com")

            assertTrue(result)
        }
    }

    @Nested
    inner class Aleatoria {
        @ParameterizedTest
        @CsvSource("email@email.com", "1234566", "qualquercoisa")
        fun `valida do tipo Aleatoria deve retornar false quando for preenchida`(chave: String) {
            val result = TipoChaveRequest.ALEATORIA.valida(chave)

            assertFalse(result)
        }

        @ParameterizedTest
        @CsvSource(",", "''")
        fun `valida do tipo Aleatoria deve retornar true quando nao for preenchida`(chave: String?) {
            val result = TipoChaveRequest.ALEATORIA.valida(chave)

            assertTrue(result)
        }
    }
}