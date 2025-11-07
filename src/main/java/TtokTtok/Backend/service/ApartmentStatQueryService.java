package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.ApartmentStatDto;

public interface ApartmentStatQueryService {
    /**
     * 저장된 전국 아파트 통계를 조회합니다.
     * (수정) keyword가 있으면 아파트명으로 필터링합니다.
     */
    ApartmentStatDto.NationwideStatResponse getNationwideStats(String keyword); // (String keyword 추가)
}