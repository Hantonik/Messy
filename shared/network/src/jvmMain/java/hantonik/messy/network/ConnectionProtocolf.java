package hantonik.messy.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.PacketListener;
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener;
import hantonik.messy.network.api.packet.handler.client.ClientsideCryptoPacketListener;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.api.packet.handler.client.ClientsideNegotiationPacketListener;
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener;
import hantonik.messy.network.api.packet.handler.server.ServersideCryptoPacketListener;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.api.packet.handler.server.ServersideNegotiationPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import hantonik.messy.network.packet.PacketFlow;
import hantonik.messy.network.packet.auth.*;
import hantonik.messy.network.packet.crypto.C2SCryptoSuccessPacket;
import hantonik.messy.network.packet.crypto.C2SCryptoInitPacket;
import hantonik.messy.network.packet.crypto.S2CCryptoDisconnectPacket;
import hantonik.messy.network.packet.crypto.S2CCryptoResponsePacket;
import hantonik.messy.network.packet.data.*;
import hantonik.messy.network.packet.negotiation.C2SNegotiationInitPacket;
import hantonik.messy.network.packet.negotiation.S2CNegotiationAckPacket;
import hantonik.messy.network.packet.negotiation.S2CNegotiationDisconnectPacket;
import hantonik.messy.util.LogUtils;
import hantonik.messy.util.Util;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum ConnectionProtocolf {
    NEGOTIATION(-1, createProtocol(S2CNegotiationDisconnectPacket::new)
            .addFlow(PacketFlow.C2S, new PacketSet<ServersideNegotiationPacketListener>()
                    .addPacket(C2SNegotiationInitPacket.class, C2SNegotiationInitPacket::new)
            )
            .addFlow(PacketFlow.S2C, new PacketSet<ClientsideNegotiationPacketListener>()
                    .addPacket(S2CNegotiationAckPacket.class, S2CNegotiationAckPacket::new)
                    .addPacket(S2CNegotiationDisconnectPacket.class, S2CNegotiationDisconnectPacket::new)
            )
    ),
    CRYPTO(0, createProtocol(S2CCryptoDisconnectPacket::new)
            .addFlow(PacketFlow.C2S, new PacketSet<ServersideCryptoPacketListener>()
                    .addPacket(C2SCryptoInitPacket.class, C2SCryptoInitPacket::new)
                    .addPacket(C2SCryptoSuccessPacket.class, C2SCryptoSuccessPacket::new)
            )
            .addFlow(PacketFlow.S2C, new PacketSet<ClientsideCryptoPacketListener>()
                    .addPacket(S2CCryptoResponsePacket.class, S2CCryptoResponsePacket::new)
                    .addPacket(S2CCryptoDisconnectPacket.class, S2CCryptoDisconnectPacket::new)
            )
    ),
    AUTH(1, createProtocol(S2CAuthDisconnectPacket::new)
            .addFlow(PacketFlow.C2S, new PacketSet<ServersideAuthPacketListener>()
                    .addPacket(C2SAuthLoginSaltRequestPacket.class, C2SAuthLoginSaltRequestPacket::new)
                    .addPacket(C2SAuthRegisterRequestPacket.class, C2SAuthRegisterRequestPacket::new)
                    .addPacket(C2SAuthSessionResumeRequestPacket.class, C2SAuthSessionResumeRequestPacket::new)
                    .addPacket(C2SAuthLogoutPacket.class, C2SAuthLogoutPacket::new)
            )
            .addFlow(PacketFlow.S2C, new PacketSet<ClientsideAuthPacketListener>()
                    .addPacket(S2CAuthSuccessResponsePacket.class, S2CAuthSuccessResponsePacket::new)
                    .addPacket(S2CAuthFailedResponsePacket.class, S2CAuthFailedResponsePacket::new)
                    .addPacket(S2CAuthDisconnectPacket.class, S2CAuthDisconnectPacket::new)
            )
    ),
    DATA(2, createProtocol(S2CDataDisconnectPacket::new)
            .addFlow(PacketFlow.C2S, new PacketSet<ServersideDataPacketListener>()
                    .addPacket(C2SDataPingPacket.class, C2SDataPingPacket::new)
                    .addPacket(C2SDataIconChangeRequestPacket.class, C2SDataIconChangeRequestPacket::new)
                    .addPacket(C2SDataPhoneChangeRequestPacket.class, C2SDataPhoneChangeRequestPacket::new)
                    .addPacket(C2SDataPasswordChangeRequestPacket.class, C2SDataPasswordChangeRequestPacket::new)
                    .addPacket(C2SDataClientDeleteRequestPacket.class, C2SDataClientDeleteRequestPacket::new)
                    .addPacket(C2SDataGatherTokensRequestPacket.class, C2SDataGatherTokensRequestPacket::new)
                    .addPacket(C2SDataGatherRelationsRequestPacket.class, C2SDataGatherRelationsRequestPacket::new)
                    .addPacket(C2SDataGatherConversationsRequestPacket.class, C2SDataGatherConversationsRequestPacket::new)
                    .addPacket(C2SDataGatherMessagesRequestPacket.class, C2SDataGatherMessagesRequestPacket::new)
                    .addPacket(C2SDataGatherStatusesRequestPacket.class, C2SDataGatherStatusesRequestPacket::new)
                    .addPacket(C2SDataRevokeTokenRequestPacket.class, C2SDataRevokeTokenRequestPacket::new)
                    .addPacket(C2SDataMutedClientPacket.class, C2SDataMutedClientPacket::new)
                    .addPacket(C2SDataUnmutedClientPacket.class, C2SDataUnmutedClientPacket::new)
                    .addPacket(C2SDataBlockedClientPacket.class, C2SDataBlockedClientPacket::new)
                    .addPacket(C2SDataUnblockedClientPacket.class, C2SDataUnblockedClientPacket::new)
                    .addPacket(C2SDataGroupCreateRequestPacket.class, C2SDataGroupCreateRequestPacket::new)
                    .addPacket(C2SDataGroupNameChangeRequestPacket.class, C2SDataGroupNameChangeRequestPacket::new)
                    .addPacket(C2SDataGroupIconChangeRequestPacket.class, C2SDataGroupIconChangeRequestPacket::new)
                    .addPacket(C2SDataGroupOwnerChangeRequestPacket.class, C2SDataGroupOwnerChangeRequestPacket::new)
                    .addPacket(C2SDataGroupAdminChangeRequestPacket.class, C2SDataGroupAdminChangeRequestPacket::new)
                    .addPacket(C2SDataGroupJoinRequestPacket.class, C2SDataGroupJoinRequestPacket::new)
                    .addPacket(C2SDataGroupLeftPacket.class, C2SDataGroupLeftPacket::new)
                    .addPacket(C2SDataConversationMutedPacket.class, C2SDataConversationMutedPacket::new)
                    .addPacket(C2SDataConversationUnmutedPacket.class, C2SDataConversationUnmutedPacket::new)
                    .addPacket(C2SDataConversationDeletedPacket.class, C2SDataConversationDeletedPacket::new)
                    .addPacket(C2SDataParticipantNicknameChangeRequestPacket.class, C2SDataParticipantNicknameChangeRequestPacket::new)
                    .addPacket(C2SDataParticipantReadOnlyChangeRequestPacket.class, C2SDataParticipantReadOnlyChangeRequestPacket::new)
                    .addPacket(C2SDataTypingPacket.class, C2SDataTypingPacket::new)
                    .addPacket(C2SDataStoppedTypingPacket.class, C2SDataStoppedTypingPacket::new)
                    .addPacket(C2SDataSentMessagePacket.class, C2SDataSentMessagePacket::new)
                    .addPacket(C2SDataReceivedMessagePacket.class, C2SDataReceivedMessagePacket::new)
                    .addPacket(C2SDataReadMessagePacket.class, C2SDataReadMessagePacket::new)
                    .addPacket(C2SDataDeletedMessagePacket.class, C2SDataDeletedMessagePacket::new)
                    .addPacket(C2SDataFileTransferRequestPacket.class, C2SDataFileTransferRequestPacket::new)
                    .addPacket(C2SDataFileTransferPacket.class, C2SDataFileTransferPacket::new)
                    .addPacket(C2SDataStatusUpdateResponsePacket.class, C2SDataStatusUpdateResponsePacket::new)
            )
            .addFlow(PacketFlow.S2C, new PacketSet<ClientsideDataPacketListener>()
                    .addPacket(S2CDataPongPacket.class, S2CDataPongPacket::new)
                    .addPacket(S2CDataIconChangeResponsePacket.class, S2CDataIconChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedIconPacket.class, S2CDataClientChangedIconPacket::new)
                    .addPacket(S2CDataPhoneChangeResponsePacket.class, S2CDataPhoneChangeResponsePacket::new)
                    .addPacket(S2CDataPasswordChangeResponsePacket.class, S2CDataPasswordChangeResponsePacket::new)
                    .addPacket(S2CDataClientDeleteResponsePacket.class, S2CDataClientDeleteResponsePacket::new)
                    .addPacket(S2CDataGatherTokensResponsePacket.class, S2CDataGatherTokensResponsePacket::new)
                    .addPacket(S2CDataGatherRelationsResponsePacket.class, S2CDataGatherRelationsResponsePacket::new)
                    .addPacket(S2CDataGatherConversationsResponsePacket.class, S2CDataGatherConversationsResponsePacket::new)
                    .addPacket(S2CDataGatherMessagesResponsePacket.class, S2CDataGatherMessagesResponsePacket::new)
                    .addPacket(S2CDataGatherStatusesResponsePacket.class, S2CDataGatherStatusesResponsePacket::new)
                    .addPacket(S2CDataRevokeTokenResponsePacket.class, S2CDataRevokeTokenResponsePacket::new)
                    .addPacket(S2CDataClientMutedClientPacket.class, S2CDataClientMutedClientPacket::new)
                    .addPacket(S2CDataClientUnmutedClientPacket.class, S2CDataClientUnmutedClientPacket::new)
                    .addPacket(S2CDataClientBlockedClientPacket.class, S2CDataClientBlockedClientPacket::new)
                    .addPacket(S2CDataClientUnblockedClientPacket.class, S2CDataClientUnblockedClientPacket::new)
                    .addPacket(S2CDataGroupCreateResponsePacket.class, S2CDataGroupCreateResponsePacket::new)
                    .addPacket(S2CDataClientCreatedGroupPacket.class, S2CDataClientCreatedGroupPacket::new)
                    .addPacket(S2CDataGroupNameChangeResponsePacket.class, S2CDataGroupNameChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedGroupNamePacket.class, S2CDataClientChangedGroupNamePacket::new)
                    .addPacket(S2CDataGroupIconChangeResponsePacket.class, S2CDataGroupIconChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedGroupIconPacket.class, S2CDataClientChangedGroupIconPacket::new)
                    .addPacket(S2CDataGroupOwnerChangeResponsePacket.class, S2CDataGroupOwnerChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedGroupOwnerPacket.class, S2CDataClientChangedGroupOwnerPacket::new)
                    .addPacket(S2CDataGroupAdminChangeResponsePacket.class, S2CDataGroupAdminChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedGroupAdminPacket.class, S2CDataClientChangedGroupAdminPacket::new)
                    .addPacket(S2CDataGroupJoinResponsePacket.class, S2CDataGroupJoinResponsePacket::new)
                    .addPacket(S2CDataClientJoinedGroupPacket.class, S2CDataClientJoinedGroupPacket::new)
                    .addPacket(S2CDataClientLeftGroupPacket.class, S2CDataClientLeftGroupPacket::new)
                    .addPacket(S2CDataClientMutedConversationPacket.class, S2CDataClientMutedConversationPacket::new)
                    .addPacket(S2CDataClientUnmutedConversationPacket.class, S2CDataClientUnmutedConversationPacket::new)
                    .addPacket(S2CDataClientDeletedConversationPacket.class, S2CDataClientDeletedConversationPacket::new)
                    .addPacket(S2CDataParticipantNicknameChangeResponsePacket.class, S2CDataParticipantNicknameChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedParticipantNicknamePacket.class, S2CDataClientChangedParticipantNicknamePacket::new)
                    .addPacket(S2CDataParticipantReadOnlyChangeResponsePacket.class, S2CDataParticipantReadOnlyChangeResponsePacket::new)
                    .addPacket(S2CDataClientChangedParticipantReadOnlyPacket.class, S2CDataClientChangedParticipantReadOnlyPacket::new)
                    .addPacket(S2CDataClientTypingPacket.class, S2CDataClientTypingPacket::new)
                    .addPacket(S2CDataClientStoppedTypingPacket.class, S2CDataClientStoppedTypingPacket::new)
                    .addPacket(S2CDataSentMessageConfirmPacket.class, S2CDataSentMessageConfirmPacket::new)
                    .addPacket(S2CDataClientSentMessagePacket.class, S2CDataClientSentMessagePacket::new)
                    .addPacket(S2CDataClientReceivedMessagePacket.class, S2CDataClientReceivedMessagePacket::new)
                    .addPacket(S2CDataReadMessageConfirmPacket.class, S2CDataReadMessageConfirmPacket::new)
                    .addPacket(S2CDataClientReadMessagePacket.class, S2CDataClientReadMessagePacket::new)
                    .addPacket(S2CDataDeletedMessageConfirmPacket.class, S2CDataDeletedMessageConfirmPacket::new)
                    .addPacket(S2CDataClientDeletedMessagePacket.class, S2CDataClientDeletedMessagePacket::new)
                    .addPacket(S2CDataFileTransferResponsePacket.class, S2CDataFileTransferResponsePacket::new)
                    .addPacket(S2CDataFileTransferResultPacket.class, S2CDataFileTransferResultPacket::new)
                    .addPacket(S2CDataStatusUpdateRequestPacket.class, S2CDataStatusUpdateRequestPacket::new)
                    .addPacket(S2CDataUpdateStatusPacket.class, S2CDataUpdateStatusPacket::new)
                    .addPacket(S2CDataDisconnectPacket.class, S2CDataDisconnectPacket::new)
            )
    );

    private static final Map<Class<? extends Packet<?>>, ConnectionProtocolf> PROTOCOL_BY_PACKET = Util.make(Maps.newHashMap(), map -> {
        for (var protocol : values()) {
            var id = protocol.getId();

            if (id < -1 || id > 1)
                throw new Error("Invalid protocol ID " + id);

            protocol.registry.forEach((_, list) -> list.getAllPackets().forEach(packet -> {
                if (map.containsKey(packet) && map.get(packet) != protocol)
                    throw new IllegalStateException("Packet " + packet + " is already assigned to protocol " + map.get(packet) + " - can't reassign to " + protocol);

                map.put(packet, protocol);
            }));
        }
    });

    @Getter
    private final int id;
    private final Map<PacketFlow, ? extends PacketSet<?>> registry;
    private final Function<String, Packet<?>> disconnectionPacket;

    private static PacketRegistryBuilder createProtocol(Function<String, Packet<?>> disconnectionPacket) {
        return new PacketRegistryBuilder(disconnectionPacket);
    }

    ConnectionProtocolf(int id, PacketRegistryBuilder registryBuilder) {
        this.id = id;
        this.registry = registryBuilder.registry;
        this.disconnectionPacket = registryBuilder.disconnectionPacket;
    }

    @Nullable
    public Integer getPacketId(PacketFlow flow, Packet<?> packet) {
        return this.registry.get(flow).getId(packet.getClass());
    }

    @Nullable
    public Packet<?> createPacket(PacketFlow flow, int id, PacketBuffer buffer) {
        return this.registry.get(flow).createPacket(id, buffer);
    }

    public Packet<?> createDisconnectPacket(String reason) {
        if (this.disconnectionPacket == null)
            return null;

        return this.disconnectionPacket.apply(reason);
    }

    public static ConnectionProtocolf getProtocolForPacket(Packet<?> packet) {
        return PROTOCOL_BY_PACKET.get(packet.getClass());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class PacketSet<T extends PacketListener> {
        private static final Logger LOGGER = LogUtils.getLogger();

        private final Map<Class<? extends Packet<T>>, Integer> classToId = Maps.newHashMap();
        private final List<Function<PacketBuffer, ? extends Packet<T>>> idToDeserializer = Lists.newArrayList();

        public <P extends Packet<T>> PacketSet<T> addPacket(Class<P> packetClass, Function<PacketBuffer, P> packetDeserializer) {
            var i = this.idToDeserializer.size();
            var j = this.classToId.put(packetClass, i);

            if (j != null) {
                LOGGER.error(LogUtils.FATAL, "Packet {} is already registered to ID {}", packetClass, j);

                throw new IllegalArgumentException("Packet " + packetClass + " is already registered to ID " + j);
            } else {
                this.idToDeserializer.add(packetDeserializer);

                return this;
            }
        }

        @Nullable
        public Integer getId(Class<?> packetClass) {
            return this.classToId.get(packetClass);
        }

        @Nullable
        public Packet<?> createPacket(int id, PacketBuffer buffer) {
            var deserializer = this.idToDeserializer.get(id);

            return deserializer != null ? deserializer.apply(buffer) : null;
        }

        public Iterable<Class<? extends Packet<?>>> getAllPackets() {
            return Iterables.unmodifiableIterable(this.classToId.keySet());
        }
    }

    @RequiredArgsConstructor
    private static class PacketRegistryBuilder {
        private final Map<PacketFlow, PacketSet<?>> registry = Maps.newEnumMap(PacketFlow.class);
        private final Function<String, Packet<?>> disconnectionPacket;

        public <T extends PacketListener> PacketRegistryBuilder addFlow(PacketFlow flow, PacketSet<T> packets) {
            this.registry.put(flow, packets);

            return this;
        }
    }
}
