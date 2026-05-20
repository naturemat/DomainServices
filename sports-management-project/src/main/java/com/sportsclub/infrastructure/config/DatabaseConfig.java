package com.sportsclub.infrastructure.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.sportsclub.infrastructure.adapter.out.persistence.postgres.repository", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
public class DatabaseConfig {
    @Bean @Primary @ConfigurationProperties("spring.datasource") public DataSourceProperties postgresDataSourceProperties() { return new DataSourceProperties(); }
    @Bean @Primary public DataSource postgresDataSource() { return postgresDataSourceProperties().initializeDataSourceBuilder().build(); }
    @Bean @Primary public LocalContainerEntityManagerFactoryBean entityManagerFactory() { LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean(); em.setDataSource(postgresDataSource()); em.setPackagesToScan("com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity"); HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter(); vendorAdapter.setGenerateDdl(true); em.setJpaVendorAdapter(vendorAdapter); return em; }
    @Bean @Primary public PlatformTransactionManager transactionManager() { JpaTransactionManager transactionManager = new JpaTransactionManager(); transactionManager.setEntityManagerFactory(entityManagerFactory().getObject()); return transactionManager; }
}