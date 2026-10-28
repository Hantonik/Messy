package hantonik.messy.network.api.packet.handler.server

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.auth.*

interface ServersideAuthPacketListener : PacketListener<ServersideAuthPacketListener> {
    fun handleRegisterRequest(packet: C2SAuthRegisterRequestPacket)

    fun handleRegisterDataResponse(packet: C2SAuthRegisterDataResponsePacket)

    fun handleLoginSaltRequest(packet: C2SAuthLoginSaltRequestPacket)

    fun handleLoginPrivateKeyRequest(packet: C2SAuthLoginPrivateKeyRequestPacket)

    fun handleLoginRequest(packet: C2SAuthLoginRequestPacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.AUTH
}