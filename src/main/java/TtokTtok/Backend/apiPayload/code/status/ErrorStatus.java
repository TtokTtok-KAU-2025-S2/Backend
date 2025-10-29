package TtokTtok.Backend.apiPayload.code.status;

import TtokTtok.Backend.apiPayload.code.BaseErrorCode;
import TtokTtok.Backend.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    USER_APT_UNIT_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4002", "사용자의 세대 정보(AptUnit)가 없습니다."),

    // 똑똑(공동탐색) 관련 에러
    KNOCK_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "KNOCK4001", "해당 '똑똑' 요청을 찾을 수 없습니다."),
    KNOCK_ALREADY_RESPONDED(HttpStatus.BAD_REQUEST, "KNOCK4002", "이미 이 요청에 응답했습니다."),
    KNOCK_REQUEST_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "KNOCK4003", "이미 종료된 '똑똑' 요청입니다."),
    KNOCK_ACTIVE_REQUEST_EXISTS(HttpStatus.BAD_REQUEST, "KNOCK4005", "이미 진행 중인 '똑똑' 요청이 있습니다. 종료 후 새로 요청해주세요."),

    // --- '똑똑' 결과 전송 관련 에러 --
    KNOCK_REPORT_NOT_REQUESTER(HttpStatus.FORBIDDEN, "KNOCK4031", "요청자 본인만 관리사무소에 전송할 수 있습니다."),
    KNOCK_REPORT_NOT_ALLOWED_ACTIVE(HttpStatus.BAD_REQUEST, "KNOCK4032", "진행 중인 '똑똑' 요청은 전송할 수 없습니다. 종료 후 시도해주세요."),
    KNOCK_ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "KNOCK4033", "이미 관리사무소에 전송된 요청입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}