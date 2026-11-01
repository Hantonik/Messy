package hantonik.messy.server.core.service;

import hantonik.messy.server.core.data.repository.ClientRelationRepository;
import hantonik.messy.server.core.data.repository.ClientRepository;
import hantonik.messy.server.core.data.repository.ConversationRepository;
import hantonik.messy.server.core.data.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final InternalConnectionService internalConnectionService;
    private final FileTransferService fileTransferService;

    private final ClientRepository clientRepository;
    private final ClientRelationRepository clientRelationRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
}
