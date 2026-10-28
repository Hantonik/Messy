package hantonik.messy.network

import dev.whyoleg.cryptography.algorithms.AES
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.PacketSendListener

interface ConnectionHandler {
    fun sendPacket(packet: Packet<*>, listener: PacketSendListener? = null)

    fun setProtocol(protocol: ConnectionProtocol)

    fun getProtocol(): ConnectionProtocol

    fun setListener(listener: PacketListener<*>)

    fun configureEncryption(key: AES.GCM.Key)

    fun disconnect(reason: String)

    fun isConnected(): Boolean
}