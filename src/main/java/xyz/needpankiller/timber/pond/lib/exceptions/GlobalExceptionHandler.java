package xyz.needpankiller.timber.pond.lib.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable throwable) {
        logger.error("An error occurred during request processing", throwable);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, throwable);
        if (throwable instanceof WebApplicationException) {
            WebApplicationException webAppException = (WebApplicationException) throwable;
            return Response.status(webAppException.getResponse().getStatus()).entity(webAppException.getResponse()).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }
}