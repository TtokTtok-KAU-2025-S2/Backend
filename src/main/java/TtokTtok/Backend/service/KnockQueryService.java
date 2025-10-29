package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.knock.KnockRequest;

public interface KnockQueryService {
    KnockRequest findKnockRequestById(Long requestId);
}