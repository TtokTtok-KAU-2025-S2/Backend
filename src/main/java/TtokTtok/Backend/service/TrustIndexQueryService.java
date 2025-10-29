package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.TrustIndexDetailDto;

public interface TrustIndexQueryService {
    /**
     * 사용자의 신뢰 지수 상세 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 신뢰 지수 상세 정보 DTO
     */
    TrustIndexDetailDto getTrustIndexDetails(Long userId);
}