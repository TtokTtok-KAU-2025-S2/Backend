package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.handler.UserHandler;
import TtokTtok.Backend.converter.TrustIndexConverter;
import TtokTtok.Backend.domain.user.TrustIndex;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.TrustIndexRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.TrustIndexDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrustIndexQueryServiceImpl implements TrustIndexQueryService {

    private final UserRepository userRepository;
    private final TrustIndexRepository trustIndexRepository; // Repository 이름 변경

    @Override
    public TrustIndexDetailDto getTrustIndexDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 월별 변화 추이 조회 (Repository 이름 변경)
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<TrustIndexRepository.MonthlyTrustChange> monthlyChanges =
                trustIndexRepository.findMonthlyChangesByUserLast6Months(userId, sixMonthsAgo);

        // 최근 활동 내역 조회 (Repository 이름 및 반환 타입 변경)
        List<TrustIndex> recentLogs = trustIndexRepository.findTop5ByUserOrderByLogDateDesc(user);

        // Converter 호출 (파라미터 타입 변경)
        return TrustIndexConverter.toTrustIndexDetailDto(user, monthlyChanges, recentLogs);
    }
}