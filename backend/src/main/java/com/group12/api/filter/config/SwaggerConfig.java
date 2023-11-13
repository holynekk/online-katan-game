package com.group12.api.filter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .securityContexts(Arrays.asList(securityContext()))
            .securitySchemes(Arrays.asList(basicAuthScheme()));
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
            .securityReferences(Arrays.asList(basicAuthReference()))
            .forPaths(PathSelectors.any())
            .build();
  }

  private SecurityScheme basicAuthScheme() {
    return new BasicAuth("basicAuth");
  }

  private SecurityReference basicAuthReference() {
    return new SecurityReference("basicAuth", new AuthorizationScope[0]);
  }
}
