package com.safetynet.alert.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.safetynet.alert.DTO.PersonDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfig {

  private final TypeResolver typeResolver;

  public SwaggerConfig(final TypeResolver typeResolver) {

    this.typeResolver = typeResolver;

  }

  @Bean
  public Docket adminApi() {

    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Adminstration")
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.safetynet.alert.controller.admin"))
        .paths(PathSelectors.any())
        .build();

  }

  @Bean
  public Docket emergencyApi() {

    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Emergency")
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.safetynet.alert.controller.emergency"))
        .paths(PathSelectors.any())
        .build()
        .additionalModels(typeResolver.resolve(PersonDto.class));

  }


}
