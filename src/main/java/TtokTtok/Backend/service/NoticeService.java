package TtokTtok.Backend.service;
//공지사항 관련 비지니스 로직
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.web.dto.NoticeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    Notice createNotice(NoticeRequest.CreateNoticeDto request);
    Notice getNotice(Long noticeId);

    // 공지사항 목록 조회 (페이징)
    Page<Notice> getNoticeList(Pageable pageable);
}