package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.web.dto.NoiseDiaryRequestDTO;
import TtokTtok.Backend.web.dto.NoiseDiaryResponseDTO;
import TtokTtok.Backend.web.dto.noise.NoiseRecordUpdateDTO;
import TtokTtok.Backend.service.NoiseRecordService;
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
    // ⭐ URL에서 /{userId} 제거
    @GetMapping("/total-count")
    public ResponseEntity<?> getTotalCount() {
        try {
            long total = noiseRecordService.getTotalCount();

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

    // ⭐ URL에서 /{userId} 제거
    @GetMapping("/monthly-count")
    public ResponseEntity<?> getMonthlyCount(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            long monthlyCount = noiseRecordService.getMonthlyCount(year, month);

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



    // ⭐ URL에서 /{userId} 제거
    @GetMapping("/average-db")
    public ResponseEntity<?> getAverageDb() {
        try {
            Double avg = noiseRecordService.getAverageDb();

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



    @PostMapping
    public ResponseEntity<?> createNoiseDiary(@RequestBody NoiseDiaryRequestDTO request) {
        NoiseDiaryResponseDTO created = noiseRecordService.createNoiseDiary(request);

        Map<String,Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code","COMMON200");
        response.put("message","소음 기록 생성 완료");
        response.put("result", created);

        return ResponseEntity.ok(response);
    }



    /**
     * 1) 소음 기록 전송 (reportYn = true, Vote 생성)
     * @param recordId 전송할 소음 기록 ID
     */
    @PostMapping("/{recordId}/send")
    public ResponseEntity<?> sendNoiseDiary(@PathVariable Long recordId) {
        Map<String, Object> response = new HashMap<>();

        try {
            noiseRecordService.sendNoiseDiary(recordId);

            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "소음 기록 전송 완료");
            response.put("result", recordId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("isSuccess", false);
            response.put("code", "NOISE4001");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }




    //record 하나 update
    @PatchMapping("/{recordId}")
    public ResponseEntity<?> UpdateRecord(@PathVariable Long recordId, @RequestBody NoiseRecordUpdateDTO request) {
        try {
            var updated = noiseRecordService.updateNoiseRecord(recordId, request);

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
            noiseRecordService.deleteNoiseRecord(recordId);

            Map<String, Object> result = new HashMap<>();
            result.put("recordId", recordId);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "삭제 완료(hard delete)");
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
