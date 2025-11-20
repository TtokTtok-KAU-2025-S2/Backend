package TtokTtok.Backend.service;
//공지사항 관련 비지니스 로직
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.web.dto.NoticeRequest;

public interface NoticeService {
    Notice createNotice(NoticeRequest.CreateNoticeDto request);
    Notice getNotice(Long noticeId);
}