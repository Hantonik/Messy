package hantonik.messy.network

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.api.packet.handler.client.ClientsideCryptoPacketListener
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener
import hantonik.messy.network.api.packet.handler.client.ClientsideNegotiationPacketListener
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.api.packet.handler.server.ServersideCryptoPacketListener
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener
import hantonik.messy.network.api.packet.handler.server.ServersideNegotiationPacketListener
import hantonik.messy.network.packet.PacketBuffer
import hantonik.messy.network.packet.PacketFlow
import hantonik.messy.network.packet.auth.*
import hantonik.messy.network.packet.crypto.C2SCryptoInitPacket
import hantonik.messy.network.packet.crypto.C2SCryptoSuccessPacket
import hantonik.messy.network.packet.crypto.S2CCryptoResponsePacket
import hantonik.messy.network.packet.negotiation.C2SNegotiationInitPacket
import hantonik.messy.network.packet.negotiation.S2CNegotiationAckPacket
import hantonik.messy.network.packet.universal.S2CUniversalDisconnectionPacket
import hantonik.messy.util.LogUtils
import io.github.oshai.kotlinlogging.KLogger
import kotlin.reflect.KClass

enum class ConnectionProtocol {
    NEGOTIATION(-1, Creator.createProtocol { S2CUniversalDisconnectionPacket<ClientsideNegotiationPacketListener>(it) }
        .addFlow(PacketFlow.C2S, PacketSet<ServersideNegotiationPacketListener>()
            .addPacket(C2SNegotiationInitPacket::class, ::C2SNegotiationInitPacket)
        )
        .addFlow(PacketFlow.S2C, PacketSet<ClientsideNegotiationPacketListener>()
            .addPacket(S2CNegotiationAckPacket::class, ::S2CNegotiationAckPacket)
            .addPacket(S2CUniversalDisconnectionPacket::class, ::S2CUniversalDisconnectionPacket)
        )
    ),
    CRYPTO(0, Creator.createProtocol { S2CUniversalDisconnectionPacket<ClientsideCryptoPacketListener>(it) }
        .addFlow(PacketFlow.C2S, PacketSet<ServersideCryptoPacketListener>()
            .addPacket(C2SCryptoInitPacket::class, ::C2SCryptoInitPacket)
            .addPacket(C2SCryptoSuccessPacket::class, ::C2SCryptoSuccessPacket)
        )
        .addFlow(PacketFlow.S2C, PacketSet<ClientsideCryptoPacketListener>()
            .addPacket(S2CCryptoResponsePacket::class, ::S2CCryptoResponsePacket)
            .addPacket(S2CUniversalDisconnectionPacket::class, ::S2CUniversalDisconnectionPacket)
        )
    ),
    AUTH(1, Creator.createProtocol { S2CUniversalDisconnectionPacket<ClientsideAuthPacketListener>(it) }
        .addFlow(PacketFlow.C2S, PacketSet<ServersideAuthPacketListener>()
            .addPacket(C2SAuthRegisterRequestPacket::class, ::C2SAuthRegisterRequestPacket)
            .addPacket(C2SAuthRegisterDataResponsePacket::class, ::C2SAuthRegisterDataResponsePacket)
            .addPacket(C2SAuthLoginSaltRequestPacket::class, ::C2SAuthLoginSaltRequestPacket)
            .addPacket(C2SAuthLoginPrivateKeyRequestPacket::class, ::C2SAuthLoginPrivateKeyRequestPacket)
            .addPacket(C2SAuthLoginRequestPacket::class, ::C2SAuthLoginRequestPacket)
        )
        .addFlow(PacketFlow.S2C, PacketSet<ClientsideAuthPacketListener>()
            .addPacket(S2CAuthRegisterDataRequestPacket::class, ::S2CAuthRegisterDataRequestPacket)
            .addPacket(S2CAuthLoginSaltResponsePacket::class, ::S2CAuthLoginSaltResponsePacket)
            .addPacket(S2CAuthLoginPrivateKeyResponsePacket::class, ::S2CAuthLoginPrivateKeyResponsePacket)
            .addPacket(S2CAuthSuccessResponsePacket::class, ::S2CAuthSuccessResponsePacket)
            .addPacket(S2CAuthFailedResponsePacket::class, ::S2CAuthFailedResponsePacket)
            .addPacket(S2CUniversalDisconnectionPacket::class, ::S2CUniversalDisconnectionPacket)
        )
    ),
    DATA(2, Creator.createProtocol { S2CUniversalDisconnectionPacket<ClientsideDataPacketListener>(it) }
        .addFlow(PacketFlow.C2S, PacketSet<ServersideDataPacketListener>()

        )
        .addFlow(PacketFlow.S2C, PacketSet<ClientsideDataPacketListener>()
            .addPacket(S2CUniversalDisconnectionPacket::class, ::S2CUniversalDisconnectionPacket)
        )
    );

    val id: Int

    private val registry: Map<PacketFlow, PacketSet<*>>
    private val disconnectionPacket: (String) -> Packet<*>

    constructor(id: Int, registryBuilder: PacketRegistryBuilder) {
        this.id = id

        this.registry = registryBuilder.registry
        this.disconnectionPacket = registryBuilder.disconnectionPacket
    }

    companion object {
        private val protocolByPacket: Map<KClass<out Packet<*>>, ConnectionProtocol> = mutableMapOf<KClass<out Packet<*>>, ConnectionProtocol>().also {
            for ((_, protocol) in it) {
                val id = protocol.id

                if (id < -1 || id > 1)
                    throw Error("Invalid packet ID $id")

                protocol.registry.forEach { (_, list) ->
                    list.getAllPackets().forEach { packetClass ->
                        if (it.containsKey(packetClass) && it[packetClass] != protocol)
                            throw IllegalStateException("Packet $packetClass is already assigned to protocol ${it[packetClass]} - cannot reassign to $protocol")

                        it[packetClass] = protocol
                    }
                }
            }
        }

        fun getProtocolForPacket(packet: Packet<*>) = protocolByPacket[packet::class]
    }

    fun getPacketId(flow: PacketFlow, packet: Packet<*>) = this.registry[flow]?.getId(packet::class)

    fun createPacket(flow: PacketFlow, id: Int, buffer: PacketBuffer) = this.registry[flow]?.createPacket(id, buffer)

    fun createDisconnectionPacket(reason: String) = this.disconnectionPacket(reason)

    private object Creator {
        fun createProtocol(disconnectionPacket: (String) -> Packet<*>) = PacketRegistryBuilder(disconnectionPacket)
    }

    private class PacketRegistryBuilder(val disconnectionPacket: (String) -> Packet<*>) {
        val registry: Map<PacketFlow, PacketSet<*>> = mutableMapOf()

        fun <T : PacketListener<T>> addFlow(flow: PacketFlow, packetSet: PacketSet<T>): PacketRegistryBuilder {
            (this.registry as MutableMap)[flow] = packetSet

            return this
        }
    }

    private class PacketSet<T : PacketListener<T>> {
        companion object {
            private val logger: KLogger = LogUtils.getLogger(PacketSet::class)
        }

        val classToId: Map<KClass<out Packet<*>>, Int> = mutableMapOf()
        val idToDeserializer: List<(PacketBuffer) -> Packet<T>> = mutableListOf()

        @Suppress("UNCHECKED_CAST")
        fun <P : Packet<*>> addPacket(packetClass: KClass<P>, packetDeserializer: (PacketBuffer) -> P): PacketSet<T> {
            val i = this.idToDeserializer.size
            val j = (this.classToId as MutableMap).put(packetClass, i)

            if (j != null) {
                logger.error(LogUtils.fatal) { "Packet $packetClass is already registered to ID $j" }

                throw IllegalArgumentException("Packet $packetClass is already registered to ID $j")
            } else {
                (this.idToDeserializer as MutableList).add(packetDeserializer as (PacketBuffer) -> Packet<T>)

                return this
            }
        }

        fun getId(packetClass: KClass<out Packet<*>>) = this.classToId[packetClass]

        fun createPacket(id: Int, buffer: PacketBuffer) = this.idToDeserializer[id](buffer)

        fun getAllPackets() = this.classToId.keys
    }
}