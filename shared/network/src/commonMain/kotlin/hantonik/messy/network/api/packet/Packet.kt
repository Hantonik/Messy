package hantonik.messy.network.api.packet

import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.PacketBuffer

interface Packet<T : PacketListener<T>> {
    fun encode(buffer: PacketBuffer) {

    }

    fun handle(handler: T)

    fun isSkippable() = false
}