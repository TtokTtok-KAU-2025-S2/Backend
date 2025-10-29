package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.complex.AptUnit;
import TtokTtok.Backend.domain.knock.KnockReport;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.KnockReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerQueryServiceImpl implements ManagerQueryService {

    private final KnockReportRepository knockReportRepository; // Repository 변경

    @Override
    public List<KnockRequest> getReportedKnocks(User manager) { // 반환 타입은 KnockRequest 유지 (컨트롤러 호환)
        // 관리자의 아파트 단지 정보 확인
        AptUnit managerApt = manager.getAptUnit();
        if (managerApt == null || managerApt.getAptCode() == null) {
            log.warn("관리자(ID: {})에게 할당된 아파트 단지 정보(aptCode)가 없습니다.", manager.getId());
            // 관리자에게 아파트 단지 정보가 없으면 빈 목록 반환
            return Collections.emptyList();
            // throw new UserHandler(ErrorStatus.USER_APT_UNIT_NOT_FOUND); // 필요시 예외 발생
        }

        String aptCode = managerApt.getAptCode();
        log.info("관리자(ID: {})가 아파트 단지 코드 '{}'의 전송된 '똑똑' 목록 조회를 요청합니다.", manager.getId(), aptCode);

        // --- 수정: KnockReportRepository 사용 및 결과 변환 ---
        // 해당 아파트 코드에 대해 보고된 KnockReport 목록 조회 (KnockRequest 포함)
        List<KnockReport> reports = knockReportRepository.findReportsByAptCodeWithKnockRequest(aptCode);

        // KnockReport 목록에서 KnockRequest만 추출하여 반환
        return reports.stream()
                .map(KnockReport::getKnockRequest)
                .collect(Collectors.toList());
        // --- 수정 종료 ---
    }
}