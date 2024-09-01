package com.nbacm.newsfeed.domain.user.common.filter;

import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(JwtUtils.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(JwtUtils.BEARER_PREFIX)) {
            String token = authHeader.substring(JwtUtils.BEARER_PREFIX.length());
            try {
                if (jwtUtils.validateToken(token)) {
                    String email = jwtUtils.getUserEmailFromToken(token);// 요청 메서드가 PUT 또는 DELETE일 때만 자신의 이메일 확인
                    if (("PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod()))) {
                        String requestedEmail = request.getParameter("email"); // URL 파라미터에서 이메일 추출
                        if (requestedEmail == null || !requestedEmail.equalsIgnoreCase(email)) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("접근이 거부되었습니다. 자신의 이메일이 아닙니다.");
                            return;
                        }
                    }
                    // 인증된 사용자 및 역할을 요청 속성에 설정
                    request.setAttribute("AuthenticatedUser", email);
                } else {
                    throw new JwtException("유효하지 않거나 이미 만료된 토큰입니다.");
                }
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT 인증 실패: " + e.getMessage());
                return;
            }
        } else if (!isExcludedPath(request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("토큰이 누락되었습니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }
    private boolean isExcludedPath(String path) {
        return "/api/v1/users/login".equalsIgnoreCase(path) || "/api/v1/users".equalsIgnoreCase(path);
    }
}

