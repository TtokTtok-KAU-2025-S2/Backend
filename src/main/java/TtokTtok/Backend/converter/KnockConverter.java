package TtokTtok.Backend.converter;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.ResponseType;
import TtokTtok.Backend.domain.knock.KnockReport;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.knock.KnockResponse;
import TtokTtok.Backend.domain.knock.KnockResult;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.web.dto.KnockResponseDto;

import java.time.LocalDateTime;

public class KnockConverter {

    public static KnockRequest toKnockRequest(User user, NoiseCategory noiseCategory, int locationScope) {
        KnockRequest request = new KnockRequest();
        request.setRequester(user);
        request.setNoiseCategory(noiseCategory); // Enum 타입
        request.setLocationScope(locationScope);
        request.setRequestTime(LocalDateTime.now());
        request.setIsActive(true);
        request.setDuration(24);

        KnockResult result = new KnockResult();
        result.setRequest(request);
        result.setTotalResponder(0);
        result.setHeardCount(0);
        result.setQuietCount(0);
        result.setPeacefulCount(0);

        request.setResult(result);

        return request;
    }

    public static KnockResponse toKnockResponse(KnockRequest request, User responder, ResponseType responseType) {
        KnockResponse response = new KnockResponse();
        response.setRequest(request);
        response.setResponder(responder);
        response.setResponseType(responseType);
        return response;
    }

    public static KnockResponseDto.CreateResultDto toCreateResultDto(KnockRequest request) {
        return KnockResponseDto.CreateResultDto.builder()
                .requestId(request.getId())
                .requestTime(request.getRequestTime())
                .noiseCategory(request.getNoiseCategory().name()) // Enum -> String
                .build();
    }

    public static KnockResponseDto.RespondResultDto toRespondResultDto(KnockResponse response) {
        return KnockResponseDto.RespondResultDto.builder()
                .responseId(response.getId())
                .requestId(response.getRequest().getId())
                .responderId(response.getResponder().getId())
                .responseType(response.getResponseType())
                .build();
    }

    public static KnockResponseDto.KnockResultDto toKnockResultDto(KnockRequest request) {
        KnockResult result = request.getResult();
        return KnockResponseDto.KnockResultDto.builder()
                .requestId(request.getId())
                .noiseCategory(request.getNoiseCategory().name()) // Enum -> String
                .requestTime(request.getRequestTime())
                .isActive(request.getIsActive())
                .totalResponder(result.getTotalResponder())
                .heardCount(result.getHeardCount())
                .quietCount(result.getQuietCount())
                .peacefulCount(result.getPeacefulCount())
                .build();
    }

    public static KnockResponseDto.ReportResultDto toReportResultDto(KnockReport knockReport) {
        return KnockResponseDto.ReportResultDto.builder()
                .reportId(knockReport.getId()) // 보고 ID
                .reportedRequestId(knockReport.getKnockRequest().getId()) // 보고된 요청 ID
                .reportedAt(knockReport.getReportTime()) // 보고 시간
                .build();
    }
}