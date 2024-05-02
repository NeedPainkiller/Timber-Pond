package xyz.needpankiller.timber.pond.lib.exceptions;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(1)
public class RuntimeExceptionHandler implements ExceptionMapper<RuntimeException> {
    private static Logger logger = LoggerFactory.getLogger(RuntimeExceptionHandler.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        logger.error("exception : {}", exception.getMessage());
        logger.error("exception Cause : {}", exception.getCause());
        exception.printStackTrace();
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, exception);
        return Response.status(response.getStatus().value()).entity(response).build();
    }
}