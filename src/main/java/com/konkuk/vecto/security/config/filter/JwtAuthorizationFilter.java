package com.konkuk.vecto.security.config.filter;

import com.konkuk.vecto.security.config.exception.BusinessExceptionHandler;
import com.konkuk.vecto.security.dto.UserDetailsDto;
import com.konkuk.vecto.security.model.common.codes.AuthConstants;
import com.konkuk.vecto.security.model.common.codes.ErrorCode;
import com.konkuk.vecto.security.model.common.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. 토큰이 필요하지 않은 API URL에 대해서 배열로 구성합니다.
        List<String> list = Arrays.asList(
                "/login",
                "/register"
        );

        // 2. 토큰이 필요하지 않은 API URL의 경우 => 로직 처리 없이 다음 필터로 이동
        if (list.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // [STEP1] Client에서 API를 요청할때 Header를 확인합니다.
        String header = request.getHeader(AuthConstants.AUTH_HEADER);
        log.info("header Check: {}", header);

        try {
            // [STEP2-1] Header 내에 토큰이 존재하는 경우
            if (header != null && !header.equalsIgnoreCase("")) {

                // [STEP2] Header 내에 토큰을 추출합니다.
                String token = TokenUtils.getTokenFromHeader(header);

                // [STEP3] 추출한 토큰이 유효한지 여부를 체크합니다.
                if (TokenUtils.isValidToken(token)) {

                    // [STEP4] 토큰을 기반으로 사용자 아이디를 반환 받는 메서드
                    String userId = TokenUtils.getUserIdFromToken(token);

                    // [STEP5] 사용자 아이디가 존재하는지 여부 체크
                    if (userId != null && !userId.equalsIgnoreCase("")) {
                        UserDetailsDto userDetailsDto=(UserDetailsDto)userService.loadUserByUsername(userId);
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(userDetailsDto, userDetailsDto.getPassword(), userDetailsDto.getAuthorities()));

                        chain.doFilter(request, response);
                    } else {
                        // 사용자 아이디가 없는 경우
                        throw new BusinessExceptionHandler("잘못된 JWT 토큰입니다.");

                    }
                    // 토큰이 유효하지 않은 경우
                } else {
                    throw new BusinessExceptionHandler("토큰의 유효기간이 만료되었습니다.");
                }
            }
            // [STEP2-1] 토큰이 존재하지 않는 경우
            else {
                throw new BusinessExceptionHandler("토큰이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            // Token 내에 Exception이 발생 하였을 경우 => 클라이언트에 응답값을 반환하고 종료합니다.
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setStatus(403);
            PrintWriter printWriter = response.getWriter();
            JSONObject jsonObject = jsonResponseWrapper(e);
            printWriter.print(jsonObject);
            printWriter.flush();
            printWriter.close();
        }
    }

    /**
     * 토큰 관련 Exception 발생 시 예외 응답값 구성
     *
     * @param e Exception
     * @return JSONObject
     */
    private JSONObject jsonResponseWrapper(Exception e) {

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("resultCode", 401);
        jsonMap.put("failMsg", e.getMessage());
        JSONObject jsonObject = new JSONObject(jsonMap);
        log.error("failMsg {}", e);
        return jsonObject;
    }
}