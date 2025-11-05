package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.ApartmentStatQueryService;
import TtokTtok.Backend.web.dto.ApartmentStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // 1. RequestParam 임포트
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class ApartmentStatController {

    private final ApartmentStatQueryService apartmentStatQueryService;

    /**
     * 전국 아파트 소음 통계 조회 API
     * (수정) "keyword" 파라미터로 아파트명 검색 기능 추가
     */
    @GetMapping("/nationwide")
    public ResponseEntity<ApiResponse<ApartmentStatDto.NationwideStatResponse>> getNationwideStats(
            // 2. (추가) keyword 파라미터 (필수 아님)
            @RequestParam(value = "keyword", required = false) String keyword
    ) {

        // 3. (수정) 서비스에 keyword 전달
        ApartmentStatDto.NationwideStatResponse response =
                apartmentStatQueryService.getNationwideStats(keyword);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}