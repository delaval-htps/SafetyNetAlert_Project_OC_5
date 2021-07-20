package com.safetynet.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** class main of application safetynet Alert .
 *
 * @author delaval */

@SpringBootApplication
@EnableSwagger2
public class SafetyNetAlertApplication {

  public static void main(String[] args) {

    SpringApplication.run(SafetyNetAlertApplication.class, args);

  }

  /**
   * Bean {@link HttpTraceRepository} to be able to have
   * trace of requests on the application in /actuator/httptrace.
   *
   * @return {@link HttpTraceRepository}
   *
   */
  @Bean
  public HttpTraceRepository httpTraceRepository() {

    return new InMemoryHttpTraceRepository();

  }
}
