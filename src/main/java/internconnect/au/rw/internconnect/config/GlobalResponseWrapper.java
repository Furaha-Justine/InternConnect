package internconnect.au.rw.internconnect.config;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Map map && map.containsKey("status") && map.containsKey("error") && map.containsKey("timestamp")) {
            return body;
        }

        String method = request.getMethod() == null ? "GET" : request.getMethod().name();
        String message = switch (method) {
            case "POST" -> "Created successfully";
            case "PUT", "PATCH" -> "Updated successfully";
            case "DELETE" -> "Deleted successfully";
            default -> "Fetched successfully";
        };

        if (body == null) {
            return Map.of("message", message);
        }
        return Map.of("message", message, "data", body);
    }
}


