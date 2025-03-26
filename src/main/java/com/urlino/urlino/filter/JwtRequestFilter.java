package com.urlino.urlino.filter;
//
//import com.urlino.urlino.entity.UserEntity;
//import com.urlino.urlino.service.impl.UserServiceImpl;
//import com.urlino.urlino.util.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserServiceImpl userService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        String userId = null;
//        String jwt = null;
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            userId = jwtUtil.extractUserId(jwt);  // 从 JWT 中提取 userId
//        }
//
//        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserEntity user = userService.findById(userId);  // 通过 userId 查找用户
//            if (user != null && jwtUtil.validateToken(jwt, user.getUserId())) {
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                        user, null, new ArrayList<>());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}


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


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        System.out.println("Request path: " + request.getRequestURI());
//
//        // 放行 OPTIONS 请求
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 登录和注册请求不做 token 校验，直接放行
//        if (path.equals("/account/login") || path.equals("/account/register")) {
//            System.out.println(" 放行 path：" + path);
//            chain.doFilter(request, response);
//            return;
//        }
//
//        final String authHeader = request.getHeader("Authorization");
//        System.out.println("🪪 Authorization Header: " + authHeader);
//        String jwt = null;
//        String userId = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            jwt = authHeader.substring(7);
//            userId = jwtUtil.extractUserId(jwt);
//            System.out.println(" Extracted userId: " + userId);
//        }
//
//        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//
//            if (jwtUtil.validateToken(jwt, userId)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//            else {
//                System.out.println("Token 校验失败！");
//            }
//        }
//
//        chain.doFilter(request, response);
//    }


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
            response.setHeader("Access-Control-Allow-Origin", "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }

}
