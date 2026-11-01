package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.conversation.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE p.client = :client")
    List<ConversationEntity> findAllByClient(@Param("client") ClientEntity client);

    @Query("SELECT c FROM ConversationEntity c WHERE TYPE(c) = DirectConversationEntity AND (TREAT(c AS DirectConversationEntity).participantA.client = :client OR TREAT(c AS DirectConversationEntity).participantB.client = :client)")
    List<ConversationEntity> findAllDirectConversationsByClient(@Param("client") ClientEntity client);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND p.client = :client")
    List<ConversationEntity> findAllGroupConversationsByClient(@Param("client") ClientEntity client);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND TREAT(c AS GroupConversationEntity).createdBy = :createdBy")
    List<ConversationEntity> findAllGroupConversationsByCreator(@Param("createdBy") ClientEntity createdBy);

    @Query("SELECT c FROM ConversationEntity c JOIN c.conversationParticipants p WHERE TYPE(c) = GroupConversationEntity AND TREAT(c AS GroupConversationEntity).owner = :owner")
    List<ConversationEntity> findAllGroupConversationsByOwner(@Param("owner") ClientEntity owner);
}
