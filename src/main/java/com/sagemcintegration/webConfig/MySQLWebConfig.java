package com.sagemcintegration.webConfig;

import com.sagemcintegration.model.mysql.ProcessedTransactions;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableJpaRepositories(
        basePackages = "com.sagemcintegration.repository.mysql",
        entityManagerFactoryRef = "MySQLEntityManager",
        transactionManagerRef = "MySQLTransactionManager"
)
@EntityScan("com.sagemcintegration.model.mysql")
public class MySQLWebConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSourceProperties MySQLDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource MySQLDataSource() {
        return MySQLDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean MySQLEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(MySQLDataSource());
        em.setPackagesToScan(
                "com.sagemcintegration.model.mysql");
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.setJpaProperties(getJpaProperties());
        return em;
    }
    @Bean
    public ProcessedTransactions processedTransactions(){
        return new ProcessedTransactions();
    }

    @Bean
    public PlatformTransactionManager MySQLTransactionManager() {

        return new JpaTransactionManager(Objects.requireNonNull(
                MySQLEntityManager().getObject()));
    }

    private Properties getJpaProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        return props;
    }
}

