package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.web.dto.NoticeRequest;
import TtokTtok.Backend.web.dto.NoticeResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class NoticeConverter {

    public static Notice toNotice(NoticeRequest.CreateNoticeDto request, User user, String imageUrl) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(imageUrl)
                .user(user)
                .apartment(user.getApartment()) // 사용자가 속한 아파트 설정
                .build();
    }

    public static NoticeResponse.NoticeDetailDto toNoticeDetailDto(Notice notice) {
        return NoticeResponse.NoticeDetailDto.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .imageUrl(notice.getImageUrl())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    public static NoticeResponse.NoticePreviewDto toNoticePreviewDto(Notice notice) {
        return NoticeResponse.NoticePreviewDto.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    // Page<Notice>를 받아서 페이징 정보가 포함된 응답으로 변환
    public static NoticeResponse.NoticeListResponse toNoticeListResponse(Page<Notice> noticePage) {
        List<NoticeResponse.NoticePreviewDto> noticePreviews = noticePage.stream()
                .map(NoticeConverter::toNoticePreviewDto)
                .collect(Collectors.toList());

        return NoticeResponse.NoticeListResponse.builder()
                .notices(noticePreviews)
                .listSize(noticePreviews.size())
                .totalPage(noticePage.getTotalPages())
                .totalElements(noticePage.getTotalElements())
                .isFirst(noticePage.isFirst())
                .isLast(noticePage.isLast())
                .build();
    }
}