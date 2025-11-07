package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.dto.NoiseCalendarDTO;
import TtokTtok.Backend.domain.noise.service.NoiseCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noise/records/calendar")
public class NoiseCalendarController {

    private final NoiseCalendarService noiseCalendarService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMonthlyNoiseCalendar(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            // 서비스 호출
            NoiseCalendarDTO data = noiseCalendarService.getMonthlyNoiseCalendar(userId, year, month);

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


    @GetMapping("/details/{userId}")
    public ResponseEntity<?> getMonthlyNoiseCalendarDetails(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            NoiseCalendarDTO data =
                    noiseCalendarService.getMonthlyNoiseCalendarDetails(userId, year, month);

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


