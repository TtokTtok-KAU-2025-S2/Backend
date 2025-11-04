package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.MonthlyReport;
import TtokTtok.Backend.repository.ApartmentRepository;
import TtokTtok.Backend.repository.MonthlyReportRepository;
import TtokTtok.Backend.web.dto.MonthlyReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // '조회' 전용이므로 readOnly=true
public class MonthlyReportQueryServiceImpl implements MonthlyReportQueryService {

    private final MonthlyReportRepository monthlyReportRepository;
    private final ApartmentRepository apartmentRepository; // (아파트 ID 검증용 - 선택 사항)
    private final ObjectMapper objectMapper; // DTO 변환 시 JSON 파싱용

    @Override
    public MonthlyReportDto.MonthlyReportResponse getMonthlyReport(Long apartmentId, LocalDate targetDate) {

        int year = targetDate.getYear();
        int month = targetDate.getMonthValue();

        // 1. 아파트 ID가 유효한지 확인 (선택 사항이지만 권장)
        // Apartment apartment = apartmentRepository.findById(apartmentId)
        //         .orElseThrow(() -> new RuntimeException("존재하지 않는 아파트입니다."));

        // 2. DB에서 해당 아파트의 특정 연/월 리포트를 조회
        MonthlyReport report = monthlyReportRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month)
                .orElseThrow(() -> new RuntimeException(year + "년 " + month + "월의 리포트가 아직 생성되지 않았습니다."));

        // 3. Entity -> DTO로 변환 (JSON 파싱 포함)
        return MonthlyReportDto.MonthlyReportResponse.fromEntity(report, objectMapper);
    }
}