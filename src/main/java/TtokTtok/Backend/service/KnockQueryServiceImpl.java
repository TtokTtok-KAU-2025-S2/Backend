package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.handler.KnockHandler;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.repository.KnockRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnockQueryServiceImpl implements KnockQueryService {

    private final KnockRequestRepository knockRequestRepository;

    @Override
    public KnockRequest findKnockRequestById(Long requestId) {
        KnockRequest knockRequest = knockRequestRepository.findById(requestId)
                .orElseThrow(() -> new KnockHandler(ErrorStatus.KNOCK_REQUEST_NOT_FOUND));

        return knockRequest;
    }
}
