package TtokTtok.Backend.service;

import java.time.LocalDate;

public interface ApartmentStatCommandService {
    /**
     * 지난달 기준으로 전국 아파트 통계를 업데이트합니다.
     * @param targetDate (기준 날짜. 11월 5일에 실행되면 10월을 기준으로 계산)
     */
    void updateNationwideStats(LocalDate targetDate);
}