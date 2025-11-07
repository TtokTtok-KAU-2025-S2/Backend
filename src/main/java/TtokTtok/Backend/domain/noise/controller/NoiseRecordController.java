package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.dto.NoiseRecordCreateDTO;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordUpdateDTO;
import TtokTtok.Backend.domain.noise.service.NoiseRecordService;
import TtokTtok.Backend.web.dto.AnalysisRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    // 오디오 파일 저장을 위한 임시 경로 (실제 프로덕션에서는 S3 등을 사용해야 함)
    private static final String FILE_STORAGE_PATH = "/tmp/noise_audios/";

    // 파일 저장 로직 (실제로는 FileService로 분리해야 함)
    private String saveAudioFile(MultipartFile file) {
        if (file.isEmpty()) return null;

        File dir = new File(FILE_STORAGE_PATH);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(FILE_STORAGE_PATH + fileName);
        try {
            file.transferTo(dest);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }

    /**
     * POST /noise/records/analyze
     * 3, 4단계: AI 분석 텍스트 생성/재생성. (파일과 메타데이터 동시 수신)
     */
    @PostMapping(value = "/analyze", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> analyzeNoise(
            @RequestPart("audioFile") MultipartFile audioFile,
            @RequestPart("data") AnalysisRequestDTO analysisRequest) {

        // Service 호출하여 AI 분석 텍스트 생성 (파일은 서비스로 전달되어 AI 분석에 사용됨)
        String aiAnalysisText = noiseRecordService.generateNoiseAnalysis(audioFile, analysisRequest);

        // 생성된 텍스트(summary)를 클라이언트에게 반환
        return ResponseEntity.ok(Map.of("summary", aiAnalysisText));
    }

    /**
     * POST /noise/records
     * 5단계: 최종 일기 저장. (파일과 전체 데이터 동시 수신)
     * 클라이언트는 이 요청에 4단계에서 받은 summary를 포함해야 합니다.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Long>> createRecord(
            @RequestPart("audioFile") MultipartFile audioFile,
            @RequestPart("data") NoiseRecordCreateDTO createRequest) {

        // 1. 오디오 파일 저장 및 경로 확보
        String audioFilePath = saveAudioFile(audioFile);

        // 2. 서비스 호출하여 모든 데이터(1~4단계 결과) 저장
        Long recordId = noiseRecordService.createNoiseRecord(createRequest, audioFile);

        // Created (201) 응답 반환
        return new ResponseEntity<>(Map.of("noiseId", recordId), HttpStatus.CREATED);
    }

    // ----------------------------------------------------
    // 소음 현황판 전송 기능 (새로운 API)
    // ----------------------------------------------------
    /**
     * POST /noise/records/{recordId}/report
     * 소음 기록을 현황판에 게시합니다. (3단계와 4단계는 건너뛸 수 있음)
     */
    @PostMapping("/{recordId}/report")
    public ResponseEntity<Map<String, Long>> reportNoiseRecord(
            @PathVariable Long recordId,
            @RequestHeader("X-USER-ID") Long userId) { // 💡 실제는 JWT/OAuth 토큰에서 userId를 추출해야 합니다.

        // Service 호출하여 기록 상태 업데이트
        Long reportedId = noiseRecordService.reportNoiseRecord(userId, recordId);

        // 200 OK 응답 및 게시된 기록 ID 반환
        return ResponseEntity.ok(Map.of("noiseId", reportedId));
    }


}

