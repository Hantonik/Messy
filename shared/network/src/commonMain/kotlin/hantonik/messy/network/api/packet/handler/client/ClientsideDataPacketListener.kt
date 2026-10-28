package hantonik.messy.network.api.packet.handler.client

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener

interface ClientsideDataPacketListener : PacketListener<ClientsideDataPacketListener> {
    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.DATA
}