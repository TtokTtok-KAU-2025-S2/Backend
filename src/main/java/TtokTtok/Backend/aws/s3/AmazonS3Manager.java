package TtokTtok.Backend.aws.s3;

import TtokTtok.Backend.config.AmazonConfig;
import TtokTtok.Backend.domain.Uuid;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;


    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        // extention 제외 : 미리 업로드 된 emotion과 구단 로고 외에 업로드하는 사진엔 .png같은 확장자가 붙어있지 않음
        // extention을 사용하면 사진이 안열리는 오류 발생
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateNoticeKeyName(Uuid uuid) {
        return amazonConfig.getNoticePath() + '/' + uuid.getUuid();
    }

    public String generateVoiceMemoKeyName(Uuid uuid) {
        return amazonConfig.getVoiceMemoPath() + '/' + uuid.getUuid();
    }
}
