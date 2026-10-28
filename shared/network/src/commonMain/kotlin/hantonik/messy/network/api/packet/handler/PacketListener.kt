package hantonik.messy.network.api.packet.handler

import hantonik.messy.network.ConnectionHandler
import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.packet.universal.S2CUniversalDisconnectionPacket

interface PacketListener<T : PacketListener<T>> {
    val connection: ConnectionHandler
    val protocol: ConnectionProtocol

    fun handleDisconnection(packet: S2CUniversalDisconnectionPacket<T>)

    fun onDisconnect(reason: String)

    fun isAcceptingPackets() = this.connection.isConnected()

    fun shouldHandlePacket(packet: Packet<*>) = this.isAcceptingPackets()
}