package com.group12.api.filter.config;

import com.group12.api.filter.SessionCookieAuthFilter;
import com.group12.api.filter.SessionCookieTokenFilter;
import com.group12.repository.UserRepository;
import com.group12.service.SessionCookieTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionCookieFilterConfig {

  @Autowired private SessionCookieTokenService tokenService;

  @Autowired private UserRepository userRepository;

  @Bean
  public FilterRegistrationBean<SessionCookieAuthFilter> sessionCookieAuthFilter() {

    FilterRegistrationBean<SessionCookieAuthFilter> registrationBean =
        new FilterRegistrationBean<>();

    registrationBean.setFilter(new SessionCookieAuthFilter(userRepository));
    registrationBean.addUrlPatterns("/api/auth/login");

    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<SessionCookieTokenFilter> sessionCookieTokenFilter() {

    FilterRegistrationBean<SessionCookieTokenFilter> registrationBean =
        new FilterRegistrationBean<>();

    registrationBean.setFilter(new SessionCookieTokenFilter(tokenService));
    registrationBean.addUrlPatterns("/api/auth/test");
    registrationBean.addUrlPatterns("/api/auth/logout");

    return registrationBean;
  }
}
