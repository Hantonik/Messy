package hantonik.messy.server.core.service;

import hantonik.messy.server.core.data.repository.FileChunkRepository;
import hantonik.messy.server.core.data.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileTransferService {
    private final FileRepository fileRepository;
    private final FileChunkRepository fileChunkRepository;
}
