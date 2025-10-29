package TtokTtok.Backend.apiPayload.exception.handler;

import TtokTtok.Backend.apiPayload.code.BaseErrorCode;
import TtokTtok.Backend.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {
    public UserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}