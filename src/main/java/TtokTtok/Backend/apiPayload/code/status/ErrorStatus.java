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

    // 사용자 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER4002", "이미 가입된 이메일입니다."),

    // 아파트 관련 에러
    APARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "APARTMENT4001", "존재하지 않는 아파트입니다."),

    //토큰 관련 에러
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 토큰입니다."),

    // 게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4001", "게시글이 없습니다."),

    // 게시글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4001", "댓글이 없습니다."),



    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),


    // Member 없음 오류
//    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다");

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4001", "해당 월의 리포트 데이터가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(this.message)
                .code(this.code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(this.message)
                .code(this.code)
                .isSuccess(false)
                .httpStatus(this.httpStatus)
                .build();
    }
}