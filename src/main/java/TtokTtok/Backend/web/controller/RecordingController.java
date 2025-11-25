package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.service.RecordingService;
import TtokTtok.Backend.web.dto.RecordingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingService recordingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecordingResponse.UploadDto> uploadVoiceRecording(
            @RequestParam("voiceFile") MultipartFile voiceFile,
            // ✨ [추가] 메타데이터 파라미터 (선택적)
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam(value = "dbMax", required = false) Double dbMax,
            @RequestParam(value = "dbAvg", required = false) Double dbAvg
    ) {
        RecordingResponse.UploadDto response = recordingService.uploadRecording(voiceFile, duration, dbMax, dbAvg);
        return ResponseEntity.ok(response);
    }

}