package com.epam.izh.rd.online.autcion.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("spring.datasource")
public class DataSourceConfiguration {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
}