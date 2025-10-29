package TtokTtok.Backend.service; // 실제 패키지 경로 확인 필요

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.handler.KnockHandler;
import TtokTtok.Backend.apiPayload.exception.handler.UserHandler;
import TtokTtok.Backend.common.enums.ResponseType;
import TtokTtok.Backend.converter.KnockConverter;
import TtokTtok.Backend.domain.complex.AptUnit;
import TtokTtok.Backend.domain.knock.KnockNotificationTarget;
import TtokTtok.Backend.domain.knock.KnockReport; // Import 추가
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.knock.KnockResponse;
import TtokTtok.Backend.domain.knock.KnockResult;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.KnockNotificationTargetRepository;
import TtokTtok.Backend.repository.KnockReportRepository; // Import 추가
import TtokTtok.Backend.repository.KnockRequestRepository;
import TtokTtok.Backend.repository.KnockResponseRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.service.KnockCommandService; // 인터페이스 경로 확인
import TtokTtok.Backend.web.dto.KnockRequestDto; // DTO 경로 확인
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Import 추가
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KnockCommandServiceImpl implements KnockCommandService {

    private static final int UNITS_PER_FLOOR = 4; // 층당 세대 수 (가정)

    private final UserRepository userRepository;
    private final KnockRequestRepository knockRequestRepository;
    private final KnockResponseRepository knockResponseRepository;
    private final KnockNotificationTargetRepository knockNotificationTargetRepository;
    private final KnockReportRepository knockReportRepository; // Repository 주입 추가

    @Override
    public KnockRequest createKnock(Long userId, KnockRequestDto.CreateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (knockRequestRepository.existsByRequesterAndIsActiveTrue(user)) {
            throw new KnockHandler(ErrorStatus.KNOCK_ACTIVE_REQUEST_EXISTS);
        }

        AptUnit userApt = user.getAptUnit();
        if (userApt == null) {
            throw new UserHandler(ErrorStatus.USER_APT_UNIT_NOT_FOUND);
        }

        List<User> neighborsToNotify = findImmediateAndDiagonalNeighbors(userApt);
        int locationScope = 8; // 최대 8세대

        neighborsToNotify.remove(user); // 자기 자신 제외

        KnockRequest newRequest = KnockConverter.toKnockRequest(user, request.getNoiseCategory(), locationScope);
        KnockRequest savedRequest = knockRequestRepository.save(newRequest); // 먼저 저장해서 ID 확보

        // 알림 대상자 저장 로직
        for (User neighbor : neighborsToNotify) {
            KnockNotificationTarget target = new KnockNotificationTarget(savedRequest, neighbor);
            knockNotificationTargetRepository.save(target); // DB에 저장
        }
        log.info("똑똑 ID: {} 알림 대상자 {}명 저장 완료.", savedRequest.getId(), neighborsToNotify.size());

        return savedRequest; // 저장된 엔티티 반환
    }

    private List<User> findImmediateAndDiagonalNeighbors(AptUnit userApt) {
        Integer currentHoNum = userApt.getHo();
        Integer dong = userApt.getDong();
        String aptCode = userApt.getAptCode();
        List<Integer> targetHoList = new ArrayList<>();

        boolean isNotFirstFloor = currentHoNum > 100;
        boolean isNotFirstUnit = currentHoNum % 100 != 1;
        boolean isNotLastUnit = currentHoNum % 100 < UNITS_PER_FLOOR;

        // 위/아래/양옆
        targetHoList.add(currentHoNum + 100);
        if (isNotFirstFloor) targetHoList.add(currentHoNum - 100);
        if (isNotFirstUnit) targetHoList.add(currentHoNum - 1);
        if (isNotLastUnit) targetHoList.add(currentHoNum + 1);
        // 대각선
        if (isNotFirstUnit) targetHoList.add(currentHoNum + 100 - 1);
        if (isNotLastUnit) targetHoList.add(currentHoNum + 100 + 1);
        if (isNotFirstFloor && isNotFirstUnit) targetHoList.add(currentHoNum - 100 - 1);
        if (isNotFirstFloor && isNotLastUnit) targetHoList.add(currentHoNum - 100 + 1);

        log.info("탐색 대상 (대각선 포함): {}단지 {}동 {}", aptCode, dong, targetHoList);
        return userRepository.findByAptUnit_AptCodeAndAptUnit_DongAndAptUnit_HoIn(aptCode, dong, targetHoList);
    }

    @Override
    public KnockResponse addResponse(Long requestId, Long userId, KnockRequestDto.RespondDto request) {
        User responder = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        KnockRequest knockRequest = knockRequestRepository.findById(requestId)
                .orElseThrow(() -> new KnockHandler(ErrorStatus.KNOCK_REQUEST_NOT_FOUND));

        if (!knockRequest.getIsActive()) {
            throw new KnockHandler(ErrorStatus.KNOCK_REQUEST_ALREADY_CLOSED);
        }

        if (knockResponseRepository.existsByRequestAndResponder(knockRequest, responder)) {
            throw new KnockHandler(ErrorStatus.KNOCK_ALREADY_RESPONDED);
        }

        // 응답 여부 업데이트 로직
        Optional<KnockNotificationTarget> targetOpt = knockNotificationTargetRepository.findByKnockRequestAndTargetUser(knockRequest, responder);
        if (targetOpt.isPresent()) {
            targetOpt.get().setResponded(true); // 응답 상태로 변경
        } else {
            log.warn("알림 대상이 아닌 사용자(ID: {})가 똑똑 요청(ID: {})에 응답했습니다.", userId, requestId);
        }

        KnockResponse newResponse = KnockConverter.toKnockResponse(knockRequest, responder, request.getResponseType());
        knockResponseRepository.save(newResponse);

        KnockResult result = knockRequest.getResult();
        result.setTotalResponder(result.getTotalResponder() + 1);

        ResponseType responseType = request.getResponseType();

        // 신뢰 지수 및 로그 업데이트 로직
        Double scoreChange;
        String logDescription;
        if (responseType == ResponseType.PEACEFUL) {
            scoreChange = 3.0;
            logDescription = "'똑똑' 도움 응답";
        } else { // HEARD or QUIET
            scoreChange = 1.0;
            logDescription = "'똑똑' 참여 응답";
        }
        responder.addTrustIndex(scoreChange, logDescription);
        log.info("User ID: {} 신뢰 지수 {}점 증가. 현재: {}", userId, scoreChange, responder.getTrustIndex());


        if (responseType == ResponseType.HEARD) {
            result.setHeardCount(result.getHeardCount() + 1);
        } else if (responseType == ResponseType.QUIET) {
            result.setQuietCount(result.getQuietCount() + 1);
        } else if (responseType == ResponseType.PEACEFUL) {
            result.setPeacefulCount(result.getPeacefulCount() + 1);
        }

        // 종료 조건 확인
        if (responseType == ResponseType.PEACEFUL || result.getTotalResponder() >= knockRequest.getLocationScope()) {
            log.info("똑똑 ID: {} 탐색 종료 조건 충족.", knockRequest.getId());
            knockRequest.setIsActive(false);
        }

        return newResponse;
    }

    // --- 관리사무소 전송 메서드 다시 추가 ---
    @Override
    public KnockReport reportKnockResult(Long requestId, Long userId) {
        KnockRequest knockRequest = knockRequestRepository.findById(requestId)
                .orElseThrow(() -> new KnockHandler(ErrorStatus.KNOCK_REQUEST_NOT_FOUND));

        // 1. 요청자 본인 확인
        if (!Objects.equals(knockRequest.getRequester().getId(), userId)) {
            throw new KnockHandler(ErrorStatus.KNOCK_REPORT_NOT_REQUESTER);
        }

        // 2. 종료된 요청인지 확인 (활성 상태면 에러)
        if (knockRequest.getIsActive()) {
            throw new KnockHandler(ErrorStatus.KNOCK_REPORT_NOT_ALLOWED_ACTIVE);
        }

        // 3. 이미 전송된 요청인지 확인 (KnockReport 테이블 확인)
        if (knockReportRepository.existsByKnockRequest(knockRequest)) {
            throw new KnockHandler(ErrorStatus.KNOCK_ALREADY_REPORTED);
        }

        // 4. KnockReport 엔티티 생성 및 저장
        KnockReport knockReport = new KnockReport(knockRequest);
        KnockReport savedReport = knockReportRepository.save(knockReport);
        log.info("똑똑 ID: {} 결과가 관리사무소로 전송 처리되었습니다 (Report ID: {}).", requestId, savedReport.getId());

        // (실제 외부 시스템 연동 로직은 여기에...)

        return savedReport; // 생성된 KnockReport 반환
    }
    // --- 추가 종료 ---
}