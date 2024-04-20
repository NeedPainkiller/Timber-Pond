package xyz.needpankiller.pond.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nullable;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua_parser.Client;
import ua_parser.Parser;
import xyz.needpankiller.pond.lib.http.AuditLogMessage;
import xyz.needpankiller.pond.lib.http.HttpMethod;
import xyz.needpankiller.pond.lib.http.HttpStatus;
import xyz.needpankiller.pond.lib.http.LoggingFilter;
import xyz.needpankiller.pond.lib.security.JsonWebTokenProvider;
import xyz.needpankiller.pond.lib.security.TokenValidFailedException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
//@Priority(Interceptor.Priority.PLATFORM_AFTER)
public class KafkaAuditService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAuditService.class);

    private static Parser uaParser = new Parser();
    @Inject
    JsonWebTokenProvider jsonWebTokenProvider;
    private static ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    @Channel("timber__topic-audit-api")
    Emitter<AuditLogMessage> auditApiEmitter;


    public void emit(HttpMethod httpMethod, int statusCode,
                      String requestURI, String requestIp, String userAgent,
                      String requestContentType, String requestPayload,
                      String responseContentType, String responsePayload,
                      @Nullable String token) {
        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogMessage auditLogMessage = new AuditLogMessage();
        auditLogMessage.setVisibleYn(true);

        Client client = uaParser.parse(userAgent);
        auditLogMessage.setAgentOs(client.os.family);
        auditLogMessage.setAgentOsVersion(client.os.major);
        auditLogMessage.setAgentBrowser(client.userAgent.family);
        auditLogMessage.setAgentBrowserVersion(client.userAgent.major);
        auditLogMessage.setAgentDevice(client.device.family);
        auditLogMessage.setRequestContentType(requestContentType);
        auditLogMessage.setRequestContentType(requestContentType);

        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        auditLogMessage.setHttpStatus(statusCode);
        auditLogMessage.setHttpMethod(httpMethod);

        auditLogMessage.setRequestIp(requestIp);
        auditLogMessage.setRequestUri(requestURI);

        auditLogMessage.setRequestPayLoad(requestPayload);
        auditLogMessage.setResponsePayLoad(responsePayload);


        Long tenantPk = null;
        Long userPk = null;
        String userId = null;
        String userName = null;
        String userEmail = null;
        Long teamPk = null;
        String teamName = null;

        try {
            if (series != null && (series.equals(HttpStatus.Series.CLIENT_ERROR) || series.equals(HttpStatus.Series.SERVER_ERROR))) {
                errorData = objectMapper.readValue(responsePayload, Map.class);
            }

            if (token != null && !token.isBlank()) {
                tenantPk = jsonWebTokenProvider.getTenantPk(token);
                userPk = jsonWebTokenProvider.getUserPk(token);
                userId = jsonWebTokenProvider.getUserId(token);
                userName = jsonWebTokenProvider.getUserName(token);
                userEmail = jsonWebTokenProvider.getUserEmail(token);
                teamPk = jsonWebTokenProvider.getTeamPk(token);
                teamName = jsonWebTokenProvider.getTeamName(token);
            }
            auditLogMessage.setTenantPk(tenantPk);
            auditLogMessage.setUserPk(userPk);
            auditLogMessage.setUserId(userId);
            auditLogMessage.setUserName(userName);
            auditLogMessage.setUserEmail(userEmail);
            auditLogMessage.setTeamPk(teamPk);
            auditLogMessage.setTeamName(teamName);

        } catch (TokenValidFailedException | JsonProcessingException | IllegalArgumentException e) {
            errorData.put("message", e.getMessage());
            errorData.put("code", e.getClass().getName());
        } finally {

            auditLogMessage.setTenantPk(tenantPk);
            auditLogMessage.setUserPk(userPk);
            auditLogMessage.setUserId(userId);
            auditLogMessage.setUserName(userName);
            auditLogMessage.setUserEmail(userEmail);
            auditLogMessage.setTeamPk(teamPk);
            auditLogMessage.setTeamName(teamName);

            auditLogMessage.setErrorData(errorData);
        }


        auditLogMessage.setApiUid(0L);
        auditLogMessage.setApiName("");
        auditLogMessage.setMenuUid(0L);
        auditLogMessage.setMenuName("");
        logger.info("{}", auditLogMessage);


        auditApiEmitter.send(auditLogMessage);
    }
}

