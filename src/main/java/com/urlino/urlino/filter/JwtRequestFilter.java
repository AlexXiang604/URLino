package com.urlino.urlino.filter;



import com.urlino.urlino.service.impl.UserDetailsServiceImpl;
import com.urlino.urlino.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 放行登录和注册
        String path = request.getRequestURI();
        if (path.equals("/account/login") || path.equals("/account/register")) {
            chain.doFilter(request, response);
            return;
        }

        //放行短链重定向
        if (request.getMethod().equals("GET") && path.matches("^/[a-zA-Z0-9]+$")) {
            chain.doFilter(request, response);
            return;
        }


        // 提取令牌
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userId = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                userId = jwtUtil.extractUserId(jwt);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                    if (jwtUtil.validateToken(jwt, userId)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        throw new ServletException("Invalid token");
                    }
                } else {
                    throw new ServletException("Missing or invalid Authorization header");
                }
            } else {
                throw new ServletException("Authorization header must start with 'Bearer '");
            }
        } catch (Exception e) {
            // 设置 CORS 头并返回 401
//            response.setHeader("Access-Control-Allow-Origin", "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/");
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000/");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }

}
