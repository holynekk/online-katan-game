package com.group12.api.filter;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.service.SessionCookieTokenService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionCookieTokenFilter extends OncePerRequestFilter {

  private SessionCookieTokenService tokenService;

  public SessionCookieTokenFilter(SessionCookieTokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isValidSessionCookie(request)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.TEXT_PLAIN_VALUE);
      response
          .getWriter()
          .print(
              "Invalid Token! Please make sure use set the X-CSRF header value as your valid session token.");
    }
  }

  private boolean isValidSessionCookie(HttpServletRequest request) {
    String providedTokenId = request.getHeader("X-CSRF");
    Optional<SessionCookieToken> token = tokenService.read(request, providedTokenId);

    if (token.isPresent()) {
      request.setAttribute(
          SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME, token.get().getUsername());
      return true;
    } else {
      return false;
    }
  }
}
