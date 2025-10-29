package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.handler.UserHandler;
import TtokTtok.Backend.common.enums.UserRole;
import TtokTtok.Backend.converter.KnockConverter;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.service.ManagerQueryService;
import TtokTtok.Backend.web.dto.KnockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/managers") // 관리자용 엔드포인트
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerQueryService managerQueryService;
    private final UserRepository userRepository; // 임시: 관리자 정보 조회용

    /**
     * 관리자에게 전송된 '똑똑' 결과 목록 조회 API
     * @param managerId // TODO: 실제 구현 시 Spring Security Context 등에서 관리자 정보 가져와야 함
     * @return List<KnockResultDto> 전송된 '똑똑' 결과 목록 (최신순)
     */
    @GetMapping("/reports")
    public ApiResponse<List<KnockResponseDto.KnockResultDto>> getReportedKnocks(
            @RequestParam("managerId") Long managerId) { // 임시 파라미터

        // --- 👇 여기가 관리자 권한 확인하는 임시 코드 ---
        // TODO: Spring Security 등으로 대체 필요
        User manager = userRepository.findById(managerId) // 1. managerId로 사용자 조회
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        if (manager.getRole() != UserRole.MANAGER) { // 2. 사용자의 role이 MANAGER인지 확인
            // 관리자가 아니면 예외 발생 (임시로 BAD_REQUEST 사용, 실제로는 FORBIDDEN이 적합)
            throw new UserHandler(ErrorStatus._BAD_REQUEST);
        }
        // --- 임시 코드 종료 ---

        // ManagerQueryService를 통해 관리자에게 보고된 KnockRequest 목록 조회
        List<KnockRequest> reportedKnocks = managerQueryService.getReportedKnocks(manager);

        // 조회된 KnockRequest 목록을 KnockResultDto 목록으로 변환
        List<KnockResponseDto.KnockResultDto> resultDtoList = reportedKnocks.stream()
                .map(KnockConverter::toKnockResultDto) // KnockConverter를 사용하여 DTO로 변환
                .collect(Collectors.toList());

        // 성공 응답 반환
        return ApiResponse.onSuccess(resultDtoList);
    }
}