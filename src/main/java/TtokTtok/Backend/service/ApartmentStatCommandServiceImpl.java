package TtokTtok.Backend.service;

import TtokTtok.Backend.common.enums.ActivationStatus; // 1. (수정) Enum 임포트
import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.ApartmentStat;
import TtokTtok.Backend.repository.ApartmentRepository;
import TtokTtok.Backend.repository.ApartmentStatRepository;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.web.dto.ReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// (구현체)
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ApartmentStatCommandServiceImpl implements ApartmentStatCommandService {

    private final ApartmentRepository apartmentRepository;
    private final NoiseDiaryRepository noiseDiaryRepository;
    private final ApartmentStatRepository apartmentStatRepository;
    private final ObjectMapper objectMapper;

    // 2. (수정) 활성화 기준을 3단계로 변경 (임의 가정)
    private static final int HIGH_THRESHOLD = 50;  // 50건 이상 HIGH
    private static final int MEDIUM_THRESHOLD = 10; // 10건 이상 MEDIUM

    @Override
    public void updateNationwideStats(LocalDate targetDate) {
        // 1. "지난달" 기준
        YearMonth lastMonth = YearMonth.from(targetDate).minusMonths(1);
        LocalDateTime startTime = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = lastMonth.atEndOfMonth().atTime(LocalTime.MAX);
        log.info("전국 아파트 통계 업데이트 시작 (기준: {})", lastMonth);

        List<Apartment> allApartments = apartmentRepository.findAll();

        for (Apartment apartment : allApartments) {
            try {
                // 2. 지난달 총 건수 조회
                Integer totalCount = noiseDiaryRepository.countByUser_ApartmentAndReportedAtBetween(
                        apartment, startTime, endTime
                );
                totalCount = (totalCount == null) ? 0 : totalCount;

                // 3. (수정) 활성화/비활성화 3단계로 결정
                ActivationStatus status;
                if (totalCount >= HIGH_THRESHOLD) {
                    status = ActivationStatus.HIGH;
                } else if (totalCount >= MEDIUM_THRESHOLD) {
                    status = ActivationStatus.MEDIUM;
                } else {
                    status = ActivationStatus.LOW;
                }

                // 4. 소음 유형 분포 집계
                List<ReportDto.CategoryStatDto> categoryStats = noiseDiaryRepository.findCategoryStatsByApartmentAndReportedAtBetween(
                        apartment, startTime, endTime
                );
                Map<String, Long> categoryMap = categoryStats.stream()
                        .collect(Collectors.toMap(
                                stat -> stat.getCategory().name(),
                                ReportDto.CategoryStatDto::getCount
                        ));
                String jsonStats = objectMapper.writeValueAsString(categoryMap);

                // 5. ApartmentStat 엔티티 찾기 (없으면 새로 생성)
                ApartmentStat stat = apartmentStatRepository.findByApartment(apartment)
                        .orElseGet(() -> ApartmentStat.builder()
                                .apartment(apartment)
                                .build());

                // 6. 데이터 업데이트
                stat.updateStats(status, jsonStats);

                // 7. 저장 (Update or Insert)
                apartmentStatRepository.save(stat);

            } catch (Exception e) {
                log.error("통계 생성 실패 (Apt ID: {}): {}", apartment.getId(), e.getMessage());
            }
        }
        log.info("전국 아파트 통계 업데이트 완료");
    }
}