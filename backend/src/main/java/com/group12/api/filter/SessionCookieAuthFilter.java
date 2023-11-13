package com.group12.api.filter;

import com.group12.api.server.UserApi;
import com.group12.constant.SessionCookieConstant;
import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.util.EncodeDecodeUtil;
import com.group12.util.EncryptDecryptUtil;
import com.group12.util.HashUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionCookieAuthFilter extends OncePerRequestFilter {

  private UserRepository userRepository;

  public SessionCookieAuthFilter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Validates if the provided authorization string is valid or not.
   *
   * @param basicAuthString - Authorization string provided by the client via request headers.
   *     Generated with username and password credentials.
   * @param request - http request sent by client
   * @return - Returns a boolean value which ensures that whether the authentication is successful
   *     or not.
   * @throws Exception - Throws exceptions when there is a problem decoding auth string.
   */
  private boolean isValidBasicAuth(String basicAuthString, HttpServletRequest request)
      throws Exception {

    if (StringUtils.isBlank(basicAuthString)) {
      return false;
    }

    try {
      String encodedAuthorizationString =
          StringUtils.substring(basicAuthString, "Basic".length()).trim();
      String plainAuthorizationString = EncodeDecodeUtil.decodeBase64(encodedAuthorizationString);
      String[] plainAuthorization = plainAuthorizationString.split(":");

      String encryptedUsername =
          EncryptDecryptUtil.encryptAes(plainAuthorization[0], UserApi.SECRET_KEY);
      String submittedPassword = plainAuthorization[1];

      Optional<User> existingData = userRepository.findByUsername(encryptedUsername);

      if (existingData.isEmpty()) {
        return false;
      }

      if (HashUtil.isBcryptMatch(submittedPassword, existingData.get().getPasswordHash())) {
        request.setAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME, encryptedUsername);
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Before hitting the endpoint, filter is applied to check if the basic authorization is valid. If
   * not returns a response with invalid credentials to warn client to re-enter its credentials.
   *
   * @param request - http request sent by client
   * @param response - response value, in case of a basic auth problem while filtering
   * @param chain - filterChain
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var basicAuthString = request.getHeader("Authorization");

    try {
      if (isValidBasicAuth(basicAuthString, request)) {
        chain.doFilter(request, response);
      } else {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.print("{\"message\":\"Invalid credential\"}");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
