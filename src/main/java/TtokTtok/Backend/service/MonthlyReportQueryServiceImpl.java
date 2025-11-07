package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.domain.MonthlyReport; // 1. (수정) MonthlyReport 엔티티 Import
import TtokTtok.Backend.repository.MonthlyReportRepository; // 2. (수정) MonthlyReportRepository Import
import TtokTtok.Backend.web.dto.MonthlyReportDto;
import com.fasterxml.jackson.databind.ObjectMapper; // 3. DTO 변환을 위해 ObjectMapper 주입
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // '조회' 전용
public class MonthlyReportQueryServiceImpl implements MonthlyReportQueryService {

    // (수정) MonthlyReportRepository 주입
    private final MonthlyReportRepository monthlyReportRepository;
    private final ObjectMapper objectMapper;
    // (NoiseDiaryRepository, ApartmentRepository 제거)

    @Override
    public MonthlyReportDto.MonthlyReportResponse getMonthlyReport(Long apartmentId, LocalDate targetDate) {

        // 1. 조회할 년/월 계산
        int year = targetDate.getYear();
        int month = targetDate.getMonthValue();

        // 2. (수정) "미리 생성된" MonthlyReport를 조회
        // (CommandService가 생성한 AI 요약본이 담긴 데이터를 여기서 읽음)
        MonthlyReport report = monthlyReportRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));

        // 3. (수정) DTO의 fromEntity 헬퍼 메서드를 사용해 변환
        // (이 과정에서 JSON이 Map으로 파싱됨)
        return MonthlyReportDto.MonthlyReportResponse.fromEntity(report, objectMapper);
    }

    // (실시간 집계 헬퍼 메서드(calculateChangeRate, createSimpleAnalysis) 모두 제거)
}