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

  /**
   * Checks if there is a valid session cookie for the current client's session.
   *
   * @param request - the http request instance sent by client
   * @param response - response value, in case of a session auth problem while filtering
   * @param filterChain - filterChain
   * @throws ServletException
   * @throws IOException
   */
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

  /**
   * Method to validate if there is a valid session cookie or not. Checks X-CSRF request header (the
   * provided token by client) and compares it with the session cookie.
   *
   * @param request - the http request instance sent by client
   * @return a boolean value - if the session cookie is still valid or not
   */
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
