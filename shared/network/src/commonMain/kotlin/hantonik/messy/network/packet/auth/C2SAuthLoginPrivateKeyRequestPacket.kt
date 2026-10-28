package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SAuthLoginPrivateKeyRequestPacket(val loginHash: ByteArray) : Packet<ServersideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readByteArray())

    override fun encode(buffer: PacketBuffer) = buffer.writeByteArray(this.loginHash)

    override fun handle(handler: ServersideAuthPacketListener) = handler.handleLoginPrivateKeyRequest(this)
}