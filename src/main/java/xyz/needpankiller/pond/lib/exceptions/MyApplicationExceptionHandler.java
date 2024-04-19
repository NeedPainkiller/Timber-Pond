package xyz.needpankiller.pond.lib.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class MyApplicationExceptionHandler implements ExceptionMapper<BusinessException> {
    private static Logger logger = LoggerFactory.getLogger(MyApplicationExceptionHandler.class);

    @Override
    public Response toResponse(BusinessException exception) {
        logger.error("handleBusinessException : {}", exception.getMessage());
        ApiErrorResponse response = ApiErrorResponse.of(exception.getErrorCode(), exception);
        return Response.status(response.getStatus().value()).entity(response).build();
    }
}