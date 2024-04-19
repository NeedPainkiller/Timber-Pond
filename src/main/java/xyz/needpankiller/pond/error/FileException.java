package xyz.needpankiller.pond.error;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class FileException extends  RuntimeException{

    @Serial
    private static final long serialVersionUID = -3643175295514351267L;
    private final ErrorCode errorCode;
    private final Map<String, Object> model;

    public FileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.model = new HashMap<>();
        this.model.put("errorCode", errorCode.getCode());
        this.model.put("errorCodeMessage", errorCode.getMessage());
        this.model.put("errorCodeStatus", errorCode.getStatus());
    }

    public FileException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.model = new HashMap<>();
        this.model.put("errorCode", errorCode.getCode());
        this.model.put("errorCodeMessage", errorCode.getMessage());
        this.model.put("errorCodeStatus", errorCode.getStatus());
        this.model.put("message", message);
    }

    public FileException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.model = model;
        this.model.put("errorCode", errorCode.getCode());
        this.model.put("errorCodeMessage", errorCode.getMessage());
        this.model.put("errorCodeStatus", errorCode.getStatus());
    }

    public FileException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(message);
        this.errorCode = errorCode;
        this.model = model;
        this.model.put("errorCode", errorCode.getCode());
        this.model.put("errorCodeMessage", errorCode.getMessage());
        this.model.put("errorCodeStatus", errorCode.getStatus());
        this.model.put("message", message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
