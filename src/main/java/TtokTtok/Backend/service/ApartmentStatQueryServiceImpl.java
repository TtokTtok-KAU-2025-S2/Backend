package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.ApartmentStat;
import TtokTtok.Backend.repository.ApartmentStatRepository;
import TtokTtok.Backend.web.dto.ApartmentStatDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartmentStatQueryServiceImpl implements ApartmentStatQueryService {

    private final ApartmentStatRepository apartmentStatRepository;
    private final ObjectMapper objectMapper;

    // (수정) String keyword 파라미터 추가
    @Override
    public ApartmentStatDto.NationwideStatResponse getNationwideStats(String keyword) {

        List<ApartmentStat> allStats;

        // 1. (수정) 키워드 유무에 따라 분기
        if (keyword == null || keyword.isBlank()) {
            // 1-1. 키워드가 없으면: (기존 로직) 통계가 있는 모든 아파트 조회
            allStats = apartmentStatRepository.findAll();
        } else {
            // 1-2. 키워드가 있으면: 아파트명으로 검색
            allStats = apartmentStatRepository.findByApartmentNameContaining(keyword);
        }

        // 2. 조회된 통계(Entity)를 DTO로 변환
        List<ApartmentStatDto.ApartmentStatDetail> dtoList = allStats.stream()
                .map(stat -> ApartmentStatDto.ApartmentStatDetail.fromEntity(stat, objectMapper))
                .collect(Collectors.toList());

        // 3. 최종 응답 반환
        return ApartmentStatDto.NationwideStatResponse.builder()
                .apartments(dtoList)
                .totalApartments(dtoList.size())
                .build();
    }
}