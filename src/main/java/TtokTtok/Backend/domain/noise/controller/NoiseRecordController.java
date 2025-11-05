package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.dto.NoiseRecordUpdateDTO;
import TtokTtok.Backend.domain.noise.service.NoiseRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/noise/records")
public class NoiseRecordController {
    private final NoiseRecordService noiseRecordService;

    public NoiseRecordController(NoiseRecordService noiseRecordService) {
        this.noiseRecordService = noiseRecordService;
    }

    //총 소음기록 수 조회
    @GetMapping("/total-count/{userId}")
    public ResponseEntity<?> getTotalCount(@PathVariable Long userId) {
        try {
            long total = noiseRecordService.getTotalCount(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", total);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/monthly-count/{userId}")
    public ResponseEntity<?> getMonthlyCount(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            long monthlyCount = noiseRecordService.getMonthlyCount(userId, year, month);

            Map<String, Object> result = new HashMap<>();
            result.put("monthlyCount", monthlyCount);
            result.put("year", year);
            result.put("month", month);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/average-db/{userId}")
    public ResponseEntity<?> getAverageDb(@PathVariable Long userId) {
        try {
            Double avg = noiseRecordService.getAverageDb(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("averageDb", avg);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    //record 하나 update
    @PatchMapping("/{recordId}")
    public ResponseEntity<?> UpdateRecord(@PathVariable Long recordId, @RequestBody NoiseRecordUpdateDTO request) {
        try {
            // TODO: 실제로는 JWT에서 userId 꺼내기
            // 지금은 다른 API처럼 PathVariable로 받을 수도 있음:
            // ex) @PatchMapping("/{userId}/{recordId}")
            Long userId = 1L; // 임시 하드코딩

            var updated = noiseRecordService.updateNoiseRecord(userId, recordId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("result", updated);
            response.put("code", "COMMON200");
            response.put("message", "수정 완료");
            response.put("isSuccess", true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);

            String code = e.getMessage();

            if ("MEMBER4001".equals(code)) {
                error.put("code", "MEMBER4001");
                error.put("message", "존재하지 않는 회원입니다.");
            } else if ("NOISE4006".equals(code)) {
                error.put("code", "NOISE4006");
                error.put("message", "수정하려는 소음 기록이 존재하지 않습니다.");
            } else if ("NOISE4007".equals(code)) {
                error.put("code", "NOISE4007");
                error.put("message", "입력값이 유효하지 않습니다. (카테고리/시간/등급/데시벨 형식 오류)");
            } else {
                error.put("code", "NOISE5000");
                error.put("message", "소음 기록 수정 중 알 수 없는 오류가 발생했습니다.");
            }

            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> DeleteRecord(@PathVariable Long recordId) {
        try {
            // TODO: 실제로는 JWT에서 userId 꺼내기
            // 지금은 다른 API처럼 PathVariable로 받을 수도 있음:
            // ex) @PatchMapping("/{userId}/{recordId}")
            Long userId = 1L; // 임시 하드코딩

            // 실제 삭제 대신 soft delete 수행
            noiseRecordService.softDeleteNoiseRecord(userId, recordId);

            Map<String, Object> result = new HashMap<>();
            result.put("recordId", recordId);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "삭제 완료(soft delete)");
            response.put("result", result);

            return ResponseEntity.ok(response);

        } catch(
                IllegalArgumentException e)

        {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);

            String code = e.getMessage();

            if ("MEMBER4001".equals(code)) {
                error.put("code", "MEMBER4001");
                error.put("message", "존재하지 않는 회원입니다.");
            } else if ("NOISE4006".equals(code)) {
                error.put("code", "NOISE4006");
                error.put("message", "삭제하려는 소음 기록이 존재하지 않습니다.");
            } else {
                error.put("code", "NOISE5000");
                error.put("message", "소음 기록 삭제 중 알 수 없는 오류가 발생했습니다.");
            }

            return ResponseEntity.badRequest().body(error);
        }
    }
}

