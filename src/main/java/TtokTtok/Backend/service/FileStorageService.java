package TtokTtok.Backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {
    private static final String FILE_STORAGE_PATH = "C:/ttokttok_storage/noise_audios/"; // Windows 예시 경로

    public String saveAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        File dir = new File(FILE_STORAGE_PATH);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(FILE_STORAGE_PATH + fileName);

        try {
            file.transferTo(dest);
            System.out.println("[FileService] 오디오 파일 저장 완료: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("오디오 파일 저장 실패: " + e.getMessage(), e);
        }
    }

}
