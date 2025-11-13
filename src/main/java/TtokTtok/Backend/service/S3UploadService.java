/*
package TtokTtok.Backend.service;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Template s3Template;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            // Or throw an exception
            return null;
        }

        // 이름이 중복되지 않도록 UUID 사용 -> 고유한 파일 이름 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_"  + originalFilename;

        // template를 사용하여 파일 업로드하고 업로드 된 리소스의 URL 가져옴
        S3Resource resource = s3Template.upload(bucketName, uniqueFilename, multipartFile.getInputStream());

        return resource.getURL().toString();
    }
}

 */