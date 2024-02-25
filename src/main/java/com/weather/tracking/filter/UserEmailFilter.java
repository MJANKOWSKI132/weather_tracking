package com.weather.tracking.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.tracking.audit.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class UserEmailFilter extends OncePerRequestFilter {
    @Autowired
    private RequestContextHolder requestContextHolder;
    private static final String USER_EMAIL = "userEmail";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> userEmailFromBody = extractUserEmailFromBody(request);
        userEmailFromBody.ifPresentOrElse(requestContextHolder::setUserEmail, () -> {
            String userEmailFromParams = request.getParameter(USER_EMAIL);
            if (userEmailFromParams != null && !userEmailFromParams.isEmpty())
                requestContextHolder.setUserEmail(userEmailFromParams);
        });
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractUserEmailFromBody(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains(MediaType.APPLICATION_JSON_VALUE))
            return Optional.empty();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null)
            stringBuilder.append(line);
        String jsonBody = stringBuilder.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonBody);
        if (!rootNode.has(USER_EMAIL))
            return Optional.empty();
        return Optional.of(rootNode.get(USER_EMAIL).asText());
    }
}

