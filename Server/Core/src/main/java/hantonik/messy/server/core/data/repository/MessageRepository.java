package hantonik.messy.server.core.data.repository;

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
    Optional<MessageEntity> findByConversationIdAndMessageIndex(UUID conversationId, Integer messageIndex);

    @Query("SELECT m FROM MessageEntity m WHERE m.conversation.id = :conversationId ORDER BY m.messageIndex DESC")
    Stream<MessageEntity> streamAllByConversationId(@Param("conversationId") UUID conversationId);

    @Query("SELECT m FROM MessageEntity m WHERE m.conversation.id = :conversationId AND m.updatedAt > :updatedAfter ORDER BY m.messageIndex DESC")
    Stream<MessageEntity> streamAllByConversationIdUpdatedAfter(@Param("conversationId") UUID conversationId, @Param("updatedAfter") Instant updatedAfter);

    Stream<MessageEntity> streamAllByConversationIdAndSenderId(UUID conversationId, UUID senderId);
}
