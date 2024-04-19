 package xyz.needpankiller.pond.lib.security;

import xyz.needpankiller.pond.lib.exceptions.BusinessException;
import xyz.needpankiller.pond.lib.exceptions.CommonErrorCode;
import xyz.needpankiller.pond.lib.exceptions.ErrorCode;

import java.util.Map;

public class TokenValidFailedException extends BusinessException {
    public TokenValidFailedException() {
        super(CommonErrorCode.TOKEN_VALIDATION_FAILED);
    }

    public TokenValidFailedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenValidFailedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TokenValidFailedException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public TokenValidFailedException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }

}