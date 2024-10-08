package com.epam.resource_processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
public class Config {


  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
