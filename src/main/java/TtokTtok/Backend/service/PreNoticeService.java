package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.PreNotice;
import TtokTtok.Backend.web.dto.PreNoticeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PreNoticeService {
    PreNotice createPreNotice(PreNoticeRequest.CreatePreNoticeDto request);
    Page<PreNotice> getPreNoticeList(Pageable pageable, Boolean filterByMyDong);
    PreNotice getPreNotice(Long preNoticeId);
    PreNotice updatePreNotice(Long preNoticeId, PreNoticeRequest.UpdatePreNoticeDto request);
    void deletePreNotice(Long preNoticeId);
}