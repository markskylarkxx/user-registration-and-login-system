package com.spring_boot_sec_app.security;

import com.spring_boot_sec_app.service.UserServiceImpl;
import com.spring_boot_sec_app.session.Session;
import com.spring_boot_sec_app.session.SessionStorage;
import com.spring_boot_sec_app.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtils tokenUtils;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SessionStorage sessionStorage;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            //every request passes through this filter chain
            String jwt = getJwtToken(request);//gets the jwt string
            System.out.println(jwt);
            if(jwt != null && tokenUtils.validateToken(jwt)) {
                // GET THE USER ID FROM THE GENERATED TOKEN
                Long userId = tokenUtils.getUserIdFromJWT(jwt);
                // LOAD THE USER DETAILS ASSOCIATED  WITH THAT TOKEN
                UserPrincipal userPrincipal = userService.loadUserById(userId);

                //check if session is available
                Optional<Session> session = sessionStorage.retrieveExistingSession(userPrincipal.getUsername());
                if(!session.isPresent()) {
                    log.error("Session not found for " + userPrincipal.getUsername());
                    filterChain.doFilter(request, response);
                    return;
                }

                //check if session has expired
                if(sessionStorage.checkSessionExpiry(session.get())) {
                    log.warn("Session has expired >>>>>>>>>>>>>>>>>>>>!");
                    filterChain.doFilter(request, response);
                    return;
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal,
                                null, userPrincipal.getAuthorities());


                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);


            }

        }catch(Exception ex) {
            //throw ex;
            ex.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

}
