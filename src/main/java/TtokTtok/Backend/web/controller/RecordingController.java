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

    /**
     * 음성 녹음 파일 업로드 API
     * (인증된 사용자만 접근 가능)
     * @param voiceFile (필수) 음성 파일 (e.g., m4a, mp3, webm...)
     * @return 저장된 S3 URL과 DB ID가 포함된 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 이 API를 호출할 수 있도록 설정
    public ResponseEntity<RecordingResponse.UploadDto> uploadVoiceRecording(
            @RequestParam("voiceFile") MultipartFile voiceFile
    ) {
        // 서비스 로직 호출 (userId 필요 없음)
        RecordingResponse.UploadDto response = recordingService.uploadRecording(voiceFile);
        return ResponseEntity.ok(response);
    }
}