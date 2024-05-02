package xyz.needpankiller.timber.pond.lib.http;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.timber.pond.helper.HttpHelper;
import xyz.needpankiller.timber.pond.service.KafkaAuditService;
import xyz.needpankiller.timber.pond.lib.security.JsonWebTokenProvider;

import java.io.IOException;

@Provider
@Priority(Interceptor.Priority.PLATFORM_AFTER)
public class LoggingFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Inject
    KafkaAuditService kafkaAuditService;

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        int statucCode = containerResponseContext.getStatus();
        HttpMethod httpMethod = HttpMethod.valueOf(containerRequestContext.getMethod());

        String requestURI = containerRequestContext.getUriInfo().getRequestUri().toString();
        String userAgent = containerRequestContext.getHeaderString("user-agent");
        String requestIp = HttpHelper.getClientIP(containerRequestContext);

        String requestContentType = containerRequestContext.getHeaderString("Content-Type");
        String requestPayload = null; // 파일 업로드 다운로드는 페이로드 로깅 제외

        String responseContentType = containerResponseContext.getHeaderString("Content-Type");
        String responsePayload = null; // 파일 업로드 다운로드는 페이로드 로깅 제외

        String token = containerRequestContext.getHeaderString(JsonWebTokenProvider.BEARER_TOKEN_HEADER);
        kafkaAuditService.emit(httpMethod, statucCode, requestURI, requestIp, userAgent, requestContentType, requestPayload, responseContentType, responsePayload, token);
    }

}