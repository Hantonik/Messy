package hantonik.messy.network.packet.auth

import hantonik.messy.data.ErrorCode
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CAuthFailedResponsePacket(val errorCode: ErrorCode) : Packet<ClientsideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readEnum<ErrorCode>())

    override fun encode(buffer: PacketBuffer) = buffer.writeEnum(this.errorCode)

    override fun handle(handler: ClientsideAuthPacketListener) = handler.handleFailedResponse(this)
}