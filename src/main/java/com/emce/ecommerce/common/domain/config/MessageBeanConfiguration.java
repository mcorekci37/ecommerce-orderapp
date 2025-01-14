package com.emce.ecommerce.common.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@RefreshScope
public class MessageBeanConfiguration {

  @Value("${spring.mvc.locale:en}")
  private String appLocale;

  @RefreshScope
  @Bean
  public SessionLocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.of(appLocale));
    return slr;
  }

  @Bean
  @RefreshScope
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setCacheSeconds(3600); // Cache for 1 hour
    return messageSource;
  }
}
