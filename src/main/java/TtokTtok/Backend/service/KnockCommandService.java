package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.knock.KnockReport;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.knock.KnockResponse;
import TtokTtok.Backend.web.dto.KnockRequestDto;

public interface KnockCommandService {
    KnockRequest createKnock(Long userId, KnockRequestDto.CreateDto request);
    KnockResponse addResponse(Long requestId, Long userId, KnockRequestDto.RespondDto request);

    KnockReport reportKnockResult(Long requestId, Long userId); // KnockRequest -> KnockReport
}