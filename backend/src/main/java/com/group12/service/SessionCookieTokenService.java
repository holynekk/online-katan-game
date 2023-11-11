package com.group12.service;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.util.SecureStringUtil;
import com.group12.util.HashUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionCookieTokenService {

  public String store(HttpServletRequest request, SessionCookieToken token) {
    var session = request.getSession(false);

    if (session != null) {
      session.invalidate();
    }

    session = request.getSession(true);

    session.setAttribute(SessionCookieConstant.SESSION_ATTRIBUTE_USERNAME, token.getUsername());

    try {
      return HashUtil.sha256(session.getId(), token.getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      return StringUtils.EMPTY;
    }
  }

  public Optional<SessionCookieToken> read(HttpServletRequest request, String providedtTokenId) {
    HttpSession session = request.getSession();

    if (session == null) {
      return Optional.empty();
    }

    String username =
        (String) session.getAttribute(SessionCookieConstant.SESSION_ATTRIBUTE_USERNAME);
    try {
      String computedTokenId = HashUtil.sha256(session.getId(), username);
      if (!SecureStringUtil.equals(providedtTokenId, computedTokenId)) {
        return Optional.empty();
      }
      SessionCookieToken token = new SessionCookieToken();
      token.setUsername(username);
      return Optional.of(token);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public void delete(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    session.invalidate();
  }
}
