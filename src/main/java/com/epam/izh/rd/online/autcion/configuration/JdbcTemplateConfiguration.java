package com.epam.izh.rd.online.autcion.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.izh.rd.online.autcion")
public class JdbcTemplateConfiguration {

    @Autowired
    private DataSourceConfiguration dataSourceConfiguration;

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dataSourceConfiguration.getUrl());
        dataSourceBuilder.driverClassName(dataSourceConfiguration.getDriverClassName());
        dataSourceBuilder.username(dataSourceConfiguration.getUsername());
        dataSourceBuilder.password(dataSourceConfiguration.getPassword());
        return dataSourceBuilder.build();
    }
}