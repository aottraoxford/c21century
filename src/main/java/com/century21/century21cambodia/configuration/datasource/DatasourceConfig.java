package com.century21.century21cambodia.configuration.datasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.century21.century21cambodia.repository")
public class DatasourceConfig {
    /*@Bean(name = "dataSource")
    public DataSource dataSource(){
        return DataSourceBuilder
                .create()
                .url("jdbc:postgresql://localhost:5432/century21cambodia")
                .username("postgres")
                .password("123")
                .driverClassName("org.postgresql.Driver")
                .build();
    }*/
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:postgresql://ec2-54-83-197-230.compute-1.amazonaws.com:5432/dddhqonrp1pk4e?sslmode=require")
                .username("farswrszhrmtbi")
                .password("c6dc339445c260fca2e2d432c357d4f5fddb300626e0b77bb35ce6699bc512fe")
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
