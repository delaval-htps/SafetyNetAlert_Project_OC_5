package com.safetynet.alert.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.safetynet.alert.dto.PersonDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration's class for swagger.
 *
 * @author delaval
 *
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {

  private final TypeResolver typeResolver;

  /**
   * Constructor with parameter TypeResolver to add additional Model for documentation.
   *
   * @param typeResolver    Object that is used for resolving generic type information of a class
   *
   */
  public SwaggerConfig(final TypeResolver typeResolver) {

    this.typeResolver = typeResolver;

  }

  /**
   * First Object Docket that is used to regroup API Operations
   * for Administration into a groupName.
   *
   * @return Docket    A builder which is intended to be the primary interface
   *                   into the Springfox framework.
   */
  @Bean
  public Docket adminApi() {

    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Adminstration")
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.safetynet.alert.controller.admin"))
        .paths(PathSelectors.any())
        .build();

  }

  /**
   * Second Object Docket that is used to regroup API operations
   * for emergency's services into a groupName.
   *
   * @return Docket    A builder which is intended to be the primary interface
   *                   into the Springfox framework.
   */
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
