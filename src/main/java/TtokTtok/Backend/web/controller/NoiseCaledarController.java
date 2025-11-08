package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.config.jwt.SecurityUtil; // ⭐ SecurityUtil import
import TtokTtok.Backend.web.dto.noise.DailyNoiseDiaryDTO;
import TtokTtok.Backend.web.dto.noise.NoiseMonthlyCalendarDTO;
import TtokTtok.Backend.service.NoiseCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noise/records/calendar")
public class NoiseCaledarController {

    private final NoiseCalendarService noiseCalendarService;

    // 💡 URL에서 /{userId} 제거
    @GetMapping
    public ResponseEntity<?> getMonthlyNoiseCalendar(
            // 💡 @PathVariable Long userId 제거
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            // ⭐ JWT 토큰에서 userId 추출
            Long userId = SecurityUtil.getCurrentUserId();

            // 서비스 호출 (수정된 서비스 메서드 시그니처에 맞춤)
            NoiseMonthlyCalendarDTO data = noiseCalendarService.getMonthlyNoiseCalendar(year, month);

            // result 구조
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("userId", userId);
            result.put("year", year);
            result.put("month", month);
            result.put("data", data);

            // 전체 응답 구조
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {  // 사용자 존재 X
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {  // 형식 오류 등
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("isSuccess", false);
            error.put("code", "CALENDAR4002");
            error.put("message", "올바르지 않은 연도 또는 월 형식입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 💡 URL에서 /details/{userId} 제거
    @GetMapping("/details")
    public ResponseEntity<?> getDailyNoiseCalendar(
            // 💡 @PathVariable Long userId 제거
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ) {
        try {
            // ⭐ JWT 토큰에서 userId 추출
            Long userId = SecurityUtil.getCurrentUserId();

            // year, month, day → LocalDate 만들어서 서비스에 넘기기
            LocalDate date = LocalDate.of(year, month, day);

            // 서비스 호출 (수정된 서비스 메서드 시그니처에 맞춤)
            DailyNoiseDiaryDTO data =
                    noiseCalendarService.getDailyNoiseDiary(date);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("userId", userId);
            result.put("year", year);
            result.put("month", month);
            result.put("data", data);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("isSuccess", false);
            error.put("code", "CALENDAR4002");
            error.put("message", "올바르지 않은 연도 또는 월 형식입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }
}

