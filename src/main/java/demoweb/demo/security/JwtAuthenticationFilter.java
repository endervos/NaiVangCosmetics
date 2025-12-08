package demoweb.demo.security;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import demoweb.demo.service.AccountService;
import demoweb.demo.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractTokenFromCookie(request);
        boolean sessionExpired = false;
        if (token != null) {
            try {
                String username = jwtTokenUtil.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = accountService.loadUserByUsername(username);
                    if (jwtTokenUtil.isInactive(token)) {
                        logger.warn("Token inactive for user: " + username);
                        sessionService.closeSessionByToken(token);
                        clearAuthCookies(response);
                        sessionExpired = true;
                        response.setHeader("X-Session-Expired", "true");
                        response.setHeader("X-Session-Reason", "inactivity");
                    } else if (jwtTokenUtil.validateToken(token, userDetails)) {
                        Optional<Session> sessionOpt = sessionService.findByToken(token);
                        if (sessionOpt.isPresent() && sessionOpt.get().getEndTime() != null) {
                            logger.warn("Session closed for user: " + username);
                            clearAuthCookies(response);
                            sessionExpired = true;
                            response.setHeader("X-Session-Expired", "true");
                            response.setHeader("X-Session-Reason", "session-closed");
                        } else {
                            boolean shouldRefresh = jwtTokenUtil.shouldRefreshToken(token);
                            String currentToken = token;
                            if (shouldRefresh) {
                                currentToken = jwtTokenUtil.refreshToken(token);
                                if (sessionOpt.isPresent()) {
                                    Session session = sessionOpt.get();
                                    sessionService.updateSessionToken(session.getSessionId(), currentToken);
                                } else {
                                    try {
                                        Account account = accountService.findAccountByEmail(username);
                                        if (account != null) {
                                            List<Session> activeSessions = sessionService.getActiveSessionsByAccount(account.getAccountId());
                                            if (!activeSessions.isEmpty()) {
                                                sessionService.updateSessionToken(activeSessions.get(0).getSessionId(), currentToken);
                                            } else {
                                                sessionService.createSession(account.getAccountId(), currentToken);
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.warn("Could not handle session: " + e.getMessage());
                                    }
                                }
                                updateAuthCookies(response, currentToken);
                            }
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: " + e.getMessage());
                clearAuthCookies(response);
                sessionExpired = true;
                response.setHeader("X-Session-Expired", "true");
                response.setHeader("X-Session-Reason", "error");
            }
        }
        if (sessionExpired && "HEAD".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void updateAuthCookies(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);
        Cookie sessionCookie = new Cookie("SESSION_ACTIVE", "true");
        sessionCookie.setHttpOnly(false);
        sessionCookie.setSecure(false);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(sessionCookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        Cookie sessionCookie = new Cookie("SESSION_ACTIVE", null);
        sessionCookie.setHttpOnly(false);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
    }
}