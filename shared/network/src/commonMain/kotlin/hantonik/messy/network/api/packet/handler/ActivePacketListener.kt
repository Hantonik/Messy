package hantonik.messy.network.api.packet.handler.server

import hantonik.messy.network.api.packet.handler.PacketListener

interface ActivePacketListener<T : ActivePacketListener<T>> : PacketListener<T> {
    fun doTasks()
}