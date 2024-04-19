package xyz.needpankiller.pond.error;

import xyz.needpankiller.pond.lib.exceptions.BusinessException;
import xyz.needpankiller.pond.lib.exceptions.ErrorCode;

import java.io.Serial;
import java.util.Map;

public class FileException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 6032256341149803392L;
    public FileException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public FileException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public FileException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }
}
