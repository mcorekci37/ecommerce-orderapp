package com.emce.ecommerce.common.domain.config;

import java.util.Locale;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class MessageConfig {

    private final MessageSource messageSource;

    public MessageConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(key, args, locale);
    }
    public String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

}
