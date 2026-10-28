package hantonik.messy.network.api.packet.handler.client

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.auth.*

interface ClientsideAuthPacketListener : PacketListener<ClientsideAuthPacketListener> {
    fun handleRegisterDataRequest(packet: S2CAuthRegisterDataRequestPacket)

    fun handleLoginSaltResponse(packet: S2CAuthLoginSaltResponsePacket)

    fun handleLoginPrivateKeyResponse(packet: S2CAuthLoginPrivateKeyResponsePacket)

    fun handleSuccessResponse(packet: S2CAuthSuccessResponsePacket)

    fun handleFailedResponse(packet: S2CAuthFailedResponsePacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.AUTH
}