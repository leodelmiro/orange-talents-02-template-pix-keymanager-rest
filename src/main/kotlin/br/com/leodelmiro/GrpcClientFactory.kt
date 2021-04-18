package br.com.leodelmiro

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChaveClientStub() = KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChaveClientStub() = KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaChaveClientStub() = KeyManagerConsultaGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChaveClientStub() = KeyManagerListaGrpcServiceGrpc.newBlockingStub(channel)

}