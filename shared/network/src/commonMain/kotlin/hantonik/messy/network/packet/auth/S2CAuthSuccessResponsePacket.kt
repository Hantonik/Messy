package hantonik.messy.network.packet.auth

import hantonik.messy.data.client.ClientProfile
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CAuthSuccessResponsePacket(val clientProfile: ClientProfile) : Packet<ClientsideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readClientProfile())

    override fun encode(buffer: PacketBuffer) = buffer.writeClientProfile(this.clientProfile)

    override fun handle(handler: ClientsideAuthPacketListener) = handler.handleSuccessResponse(this)
}