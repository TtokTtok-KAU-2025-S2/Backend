package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.dto.NoiseRecordDTO;
import TtokTtok.Backend.domain.noise.service.NoiseRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/noise/records")
public class NoiseRecordController {

    private final NoiseRecordService service;

    public NoiseRecordController(NoiseRecordService service) {
        this.service = service;
    }


    //생성
    @PostMapping
    public ResponseEntity<?> createRecord(
            @RequestParam Long userId,
            @RequestBody NoiseRecordDTO dto) {

        try {
            NoiseRecordDTO created = service.createRecord(userId, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON201");
            response.put("message", "등록 완료");
            response.put("result", created);
            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4003");
            error.put("message", "등록 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //조회
    @GetMapping
    public ResponseEntity<?> getRecordsByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            List<NoiseRecordDTO> records = service.getRecordsByDate(userId, date);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("date", date);
            result.put("records", records);

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
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4002");
            error.put("message", "올바르지 않은 날짜 형식입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //수정
    @PatchMapping("/{recordId}")
    public ResponseEntity<?> updateRecord(
            @PathVariable Long recordId,
            @RequestBody NoiseRecordDTO dto) {

        try {
            NoiseRecordDTO updated = service.updateRecord(recordId, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "수정 완료");
            response.put("result", updated);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4002");
            error.put("message", "수정 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //삭제
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(
            @PathVariable Long recordId) {

        try {
            service.deleteRecord(recordId);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "삭제 완료");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "RECORD4004");
            error.put("message", "삭제 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(error);
        }

    }

}
