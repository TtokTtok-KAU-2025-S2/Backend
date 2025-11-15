package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.RecordingResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RecordingService {

    /**
     * 음성 녹음 파일을 S3에 업로드하고 DB에 저장
     * (사용자 정보는 SecurityContext에서 가져옴)
     */
    RecordingResponse.UploadDto uploadRecording(MultipartFile voiceFile);
}