package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SAuthLoginSaltRequestPacket(val email: String) : Packet<ServersideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readUtf(255))

    override fun encode(buffer: PacketBuffer) = buffer.writeUtf(this.email, 255)
    
    override fun handle(handler: ServersideAuthPacketListener) = handler.handleLoginSaltRequest(this)
}