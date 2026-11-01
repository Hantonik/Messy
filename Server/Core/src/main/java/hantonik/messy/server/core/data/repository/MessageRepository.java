package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.conversation.ConversationEntity;
import hantonik.messy.server.core.data.entity.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Optional<MessageEntity> findByConversationAndMessageIndex(ConversationEntity conversation, Integer messageIndex);

    @Query("SELECT m FROM MessageEntity m WHERE m.conversation = :conversation ORDER BY m.messageIndex DESC")
    Stream<MessageEntity> streamAllByConversation(@Param("conversation") ConversationEntity conversation);

    @Query("SELECT m FROM MessageEntity m WHERE m.conversation = :conversation AND m.updatedAt > :updatedAfter ORDER BY m.messageIndex DESC")
    Stream<MessageEntity> streamAllByConversationUpdatedAfter(@Param("conversation") ConversationEntity conversation, @Param("updatedAfter") Instant updatedAfter);

    Stream<MessageEntity> streamAllByConversationAndSender(ConversationEntity conversation, ClientEntity sender);
}
