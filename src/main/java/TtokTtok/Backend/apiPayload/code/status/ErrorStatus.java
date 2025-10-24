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

    // Authentication 관련 오류
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4001", "사용자를 찾을 수 없습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH4002", "비밀번호가 일치하지 않습니다"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4003", "이미 존재하는 닉네임입니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4004", "비밀번호가 일치하지 않습니다"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "AUTH4005", "비밀번호 형식이 올바르지 않습니다"),
    
    // Apartment 관련 오류
    APARTMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "APT4001", "아파트를 찾을 수 없습니다"),
    APT_UNIT_NOT_FOUND(HttpStatus.BAD_REQUEST, "APT4002", "세대 정보를 찾을 수 없습니다"),
    MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "APT4003", "관리사무소 정보를 찾을 수 없습니다");


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