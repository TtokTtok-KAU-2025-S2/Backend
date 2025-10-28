package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.service.NoiseAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/noise/analysis")
public class NoiseAnalysisController {
    private final NoiseAnalysisService noiseAnalysisService;

    public NoiseAnalysisController(NoiseAnalysisService noiseAnalysisService) {
        this.noiseAnalysisService = noiseAnalysisService;
    }

    @GetMapping("/monthly/count")
    public ResponseEntity<Map<String, Object>> getMonthlyNoiseCount(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {

        try {
            Long totalNoiseCount = noiseAnalysisService.getMonthlyNoiseCount(userId, year, month);

            // 성공 응답 (200 OK)
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("year", year);
            result.put("month", month);
            result.put("totalNoiseCount", totalNoiseCount);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "월간 총 소음 기록 수 조회 성공");
            response.put("result", result);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 실패 응답 처리 (존재하지 않는 회원 or 유효하지 않은 연월)
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);

            if (e.getMessage().contains("존재하지 않는 회원")) {
                error.put("code", "MEMBER4001");
                error.put("message", "존재하지 않는 회원입니다.");
            } else {
                // 유효하지 않은 연도/월 형식 (Service에서 던진 예외)
                error.put("code", "ANALYSIS4001");
                error.put("message", "올바르지 않은 연도 또는 월 형식입니다.");
            }
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            // 기타 예외 (500 Internal Server Error)
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "SERVER500");
            error.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }


    @GetMapping("/average-intensity")
    public ResponseEntity<Map<String, Object>> getMonthlyAverageIntensity(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {

        try {
            Double averageIntensity = noiseAnalysisService.getMonthlyAverageIntensity(userId, year, month);

            // 성공 응답 (200 OK)
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("year", year);
            result.put("month", month);
            result.put("averageIntensity", averageIntensity); // Service에서 이미 포맷팅된 값 사용

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "월간 소음 평균 강도 조회 성공");
            response.put("result", result);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 실패 응답 처리 (기존 로직 재사용)
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);

            if (e.getMessage().contains("존재하지 않는 회원")) {
                error.put("code", "MEMBER4001");
                error.put("message", "존재하지 않는 회원입니다.");
            } else {
                error.put("code", "ANALYSIS4001");
                error.put("message", "올바르지 않은 연도 또는 월 형식입니다.");
            }
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            // 기타 예외 (500 Internal Server Error)
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "SERVER500");
            error.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

}
