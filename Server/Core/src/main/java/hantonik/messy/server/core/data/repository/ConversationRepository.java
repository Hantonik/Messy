package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.conversation.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE p.client.id = :clientId")
    List<ConversationEntity> findAllByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT c FROM ConversationEntity c WHERE TYPE(c) = DirectConversationEntity AND (TREAT(c AS DirectConversationEntity).client_a = :clientId OR TREAT(c AS DirectConversationEntity).client_b = :clientId)")
    List<ConversationEntity> findAllDirectConversationsByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND p.client.id = :clientId")
    List<ConversationEntity> findAllGroupConversationsByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND TREAT(c AS GroupConversationEntity).creator.id = :creatorId")
    List<ConversationEntity> findAllGroupConversationsByCreatorId(@Param("creatorId") UUID creatorId);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND TREAT(c AS GroupConversationEntity).owner.id = :ownerId")
    List<ConversationEntity> findAllGroupConversationsByOwnerId(@Param("ownerId") UUID ownerId);
}
