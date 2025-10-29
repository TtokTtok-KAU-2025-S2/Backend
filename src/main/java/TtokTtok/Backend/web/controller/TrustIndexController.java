package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.TrustIndexQueryService;
import TtokTtok.Backend.web.dto.TrustIndexDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users") // 사용자 관련 엔드포인트이므로 /users 하위로
@RequiredArgsConstructor
public class TrustIndexController {

    private final TrustIndexQueryService trustIndexQueryService;

    /**
     * 신뢰 지수 상세 정보 조회 API
     * @param userId 조회할 사용자 ID
     * @return 신뢰 지수 상세 정보
     */
    @GetMapping("/{userId}/trust-index/details")
    public ApiResponse<TrustIndexDetailDto> getTrustIndexDetails(@PathVariable Long userId) {
        TrustIndexDetailDto detailDto = trustIndexQueryService.getTrustIndexDetails(userId);
        return ApiResponse.onSuccess(detailDto);
    }
}
