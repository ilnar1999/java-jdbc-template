package com.epam.izh.rd.online.autcion.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ComponentScan("com.epam.izh.rd.online.autcion")
public class JdbcTemplateConfiguration {
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(ClassLoader.getSystemResource("application.properties").getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataSourceBuilder.url(properties.getProperty("spring.datasource.url"));
        dataSourceBuilder.driverClassName(properties.getProperty("spring.datasource.driverClassName"));
        dataSourceBuilder.username(properties.getProperty("spring.datasource.username"));
        dataSourceBuilder.password(properties.getProperty("spring.datasource.password"));
        return dataSourceBuilder.build();
    }
}