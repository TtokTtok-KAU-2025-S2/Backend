package TtokTtok.Backend.service.scheduler; // 실제 패키지 경로 확인 필요

import TtokTtok.Backend.domain.knock.KnockNotificationTarget; // Import 추가
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User; // Import 추가
import TtokTtok.Backend.repository.KnockNotificationTargetRepository; // Import 추가
import TtokTtok.Backend.repository.KnockRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnockSchedulerService {

    private final KnockRequestRepository knockRequestRepository;
    private final KnockNotificationTargetRepository knockNotificationTargetRepository; // Repository 주입

    private static final int PENALTY_THRESHOLD = 3; // 미응답 페널티 기준 횟수
    private static final Double PENALTY_AMOUNT = -0.5; // 감점량
    private static final String PENALTY_DESCRIPTION = "'똑똑' 미응답 페널티"; // 로그 설명

    @Scheduled(fixedDelay = 600000) // 10분마다 실행
    @Transactional
    public void checkAndCloseExpiredKnocks() {
        log.info("[Scheduler] 24시간 초과된 '똑똑' 요청 자동 종료 및 미응답 페널티 처리 스케줄러 실행...");

        LocalDateTime expirationLimit = LocalDateTime.now().minusHours(24);
        List<KnockRequest> expiredRequests = knockRequestRepository.findByIsActiveTrueAndRequestTimeBefore(expirationLimit);

        if (expiredRequests.isEmpty()) {
            log.info("[Scheduler] 자동 종료할 '똑똑' 요청이 없습니다.");
            return;
        }

        int totalClosed = 0;
        int totalPenaltyApplied = 0;

        for (KnockRequest request : expiredRequests) {
            // 1. 요청 비활성화
            request.setIsActive(false);
            log.info("[Scheduler] 똑똑 ID: {} (생성시간: {}) 24시간 초과로 자동 종료 처리.", request.getId(), request.getRequestTime());
            totalClosed++;

            // --- 2. 미응답자 페널티 적용 로직 추가 ---
            List<KnockNotificationTarget> missedTargets = knockNotificationTargetRepository.findByKnockRequestAndRespondedFalse(request);
            for (KnockNotificationTarget target : missedTargets) {
                User missedUser = target.getTargetUser();
                // User 엔티티가 null이 아닌지 확인 (데이터 정합성 방어)
                if (missedUser != null) {
                    missedUser.setMissedKnockCount(missedUser.getMissedKnockCount() + 1);
                    log.info("[Scheduler] User ID: {} 미응답 횟수 증가. 현재: {}", missedUser.getId(), missedUser.getMissedKnockCount());

                    // 페널티 기준 횟수 도달 시 감점 및 카운트 리셋
                    if (missedUser.getMissedKnockCount() >= PENALTY_THRESHOLD) {
                        missedUser.addTrustIndex(PENALTY_AMOUNT, PENALTY_DESCRIPTION); // User 편의 메서드 사용
                        log.warn("[Scheduler] User ID: {} 미응답 {}회 누적으로 신뢰 지수 {}점 감점. 현재: {}", missedUser.getId(), PENALTY_THRESHOLD, PENALTY_AMOUNT, missedUser.getTrustIndex());
                        missedUser.setMissedKnockCount(0); // 카운트 초기화
                        totalPenaltyApplied++;
                    }
                } else {
                    log.error("[Scheduler] KnockNotificationTarget ID: {} 에 연결된 User 정보가 없습니다. 확인 필요.", target.getId());
                }
            }
            // --- 로직 추가 종료 ---
        }

        log.info("[Scheduler] 총 {} 건의 '똑똑' 요청 자동 종료 완료. 미응답 페널티 {} 건 적용.", totalClosed, totalPenaltyApplied);
    }
}