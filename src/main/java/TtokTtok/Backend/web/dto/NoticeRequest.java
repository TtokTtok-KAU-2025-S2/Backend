package TtokTtok.Backend.web.dto;
//클라이언트에서 공지사항을 생성하거나 수정할 때 보낼 데이터를 담는 객체

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class NoticeRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateNoticeDto {
        private String title;
        private String content;

        MultipartFile noticePicture;
    }
}