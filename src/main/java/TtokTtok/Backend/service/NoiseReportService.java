package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.NoiseReportResponse;
import org.springframework.data.domain.Pageable;

public interface NoiseReportService {
    NoiseReportResponse.NoiseReportListResponse getNoiseReportList(Pageable pageable);

    NoiseReportResponse.NoiseReportDetailDto getNoiseReportDetail(Long reportId);
}
