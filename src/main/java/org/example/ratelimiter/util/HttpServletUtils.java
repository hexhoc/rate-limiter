package org.example.ratelimiter.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpServletUtils {

    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    public static HttpServletRequest getRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }

    private static ServletRequestAttributes getServletRequestAttributes() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException("not request");
        }
        return requestAttributes;
    }

}