package hantonik.messy.network.api.packet.handler.server

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener

interface ServersideDataPacketListener : PacketListener<ServersideDataPacketListener> {
    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.DATA
}