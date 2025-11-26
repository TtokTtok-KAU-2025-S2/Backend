package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.RecordingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecordingService {

    /**
     * 음성 녹음 파일을 S3에 업로드하고 DB에 저장
     * (사용자 정보는 SecurityContext에서 가져옴)
     */

    /**
     * 사용자의 모든 녹음 파일 목록을 최신순으로 조회
     */
    List<RecordingResponse.RecordDto> getAllRecordings();
    RecordingResponse.UploadDto uploadRecording(MultipartFile voiceFile, Integer duration, Double dbMax, Double dbAvg);
}