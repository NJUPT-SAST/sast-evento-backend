package sast.evento.config;

import com.zaxxer.hikari.HikariDataSource;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Value("${spring.quartz.datasource.url}")
    private String url;
    @Value("${spring.quartz.datasource.username}")
    private String username;
    @Value("${spring.quartz.datasource.password}")
    private String password;
    @Value("${spring.quartz.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.quartz.datasource.name}")
    private String name;

    @Bean(name = "schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(quartzDataSource());
        schedulerFactoryBean.setTransactionManager(quartzDataSourceTransactionManager());
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setSchedulerName("quartzScheduler");
        schedulerFactoryBean.setSchedulerFactoryClass(StdSchedulerFactory.class);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setExposeSchedulerInRepository(true);
        return schedulerFactoryBean;
    }

    private HikariDataSource quartzDataSource() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(url);
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);
        dataSourceProperties.setDriverClassName(driverClassName);
        dataSourceProperties.setName(name);
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private DataSourceTransactionManager quartzDataSourceTransactionManager() {
        return new DataSourceTransactionManager(quartzDataSource());
    }

}
