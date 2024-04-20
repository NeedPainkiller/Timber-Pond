package xyz.needpankiller.pond.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import xyz.needpankiller.pond.lib.exceptions.BusinessException;
import xyz.needpankiller.pond.lib.security.JsonWebTokenProvider;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static xyz.needpankiller.pond.lib.exceptions.CommonErrorCode.HTTP_RESULT_PARSE_ERROR;

public class HttpHelper {

    private static ObjectMapper objectMapper = createObjectMapper();

    public static ObjectMapper createObjectMapper() {
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

    public static String getClientIP(ContainerRequestContext containerRequestContext) {
        String ip = containerRequestContext.getHeaderString("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("X-RealIP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getHeaderString("REMOTE_ADDR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = containerRequestContext.getUriInfo().getRequestUri().getHost();
        }
        return ip;
    }

    public static Map<String, String> convertHttpResultToMap(String source) throws BusinessException {
        try {
            String reformat = source.replace(",", "&").replace(":", "=").replace("\"", "").replace(" ", "").replace("\n", "").replace("}", "").replace("{", "");

            return objectMapper.readValue(reformat, Map.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(HTTP_RESULT_PARSE_ERROR, e.getMessage());
        }
    }

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private static final String POST_METHOD = "POST";
    private static final String MULTIPART = "multipart/";

}