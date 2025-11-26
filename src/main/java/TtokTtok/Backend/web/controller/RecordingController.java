package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse; // ✨ ApiResponse 임포트 확인
import TtokTtok.Backend.service.RecordingService;
import TtokTtok.Backend.web.dto.RecordingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingService recordingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<RecordingResponse.RecordDto>> getAllRecordings() {
        return ApiResponse.onSuccess(recordingService.getAllRecordings());
    }

    // ✨ [수정] 반환 타입을 ResponseEntity -> ApiResponse로 변경
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecordingResponse.UploadDto> uploadVoiceRecording(
            @RequestParam("voiceFile") MultipartFile voiceFile,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam(value = "dbMax", required = false) Double dbMax,
            @RequestParam(value = "dbAvg", required = false) Double dbAvg
    ) {
        // 서비스 호출
        RecordingResponse.UploadDto response = recordingService.uploadRecording(voiceFile, duration, dbMax, dbAvg);

        // ApiResponse로 감싸서 반환
        return ApiResponse.onSuccess(response);
    }
}