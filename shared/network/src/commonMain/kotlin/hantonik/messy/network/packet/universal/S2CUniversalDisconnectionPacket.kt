package hantonik.messy.network.packet.universal

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CUniversalDisconnectionPacket<T : PacketListener<T>>(val reason: String) : Packet<T> {
    constructor(buffer: PacketBuffer) : this(buffer.readUtf(0x40000))

    override fun encode(buffer: PacketBuffer) = buffer.writeUtf(this.reason, 0x40000)

    override fun handle(handler: T) = handler.handleDisconnection(this)
}