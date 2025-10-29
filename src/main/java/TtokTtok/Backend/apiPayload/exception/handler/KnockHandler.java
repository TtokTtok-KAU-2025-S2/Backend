package TtokTtok.Backend.apiPayload.exception.handler;

import TtokTtok.Backend.apiPayload.code.BaseErrorCode;
import TtokTtok.Backend.apiPayload.exception.GeneralException;

public class KnockHandler extends GeneralException {
    public KnockHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}