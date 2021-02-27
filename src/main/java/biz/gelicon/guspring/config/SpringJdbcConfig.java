package biz.gelicon.guspring.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.h2.security.auth.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
@Tag(name = "Конфигурация JDBC", description = "Создает JdbcTemplate к другим базам данных ")
public class SpringJdbcConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringJdbcConfig.class);

    @Autowired
    private Environment environment;

    /**
     * Создание второй JdbcTemplate
     * Секция в application.properties - second.datasource
     * Если нет - возвращает null, не валится
     */
    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate() {
        String url = environment.getProperty("second.datasource.url");
        if (url == null) { return null;}
        String driverClassName;
        if (url.contains("jdbc:postgresql")) {
            driverClassName = "org.postgresql.Driver";
        } else if (url.contains("jdbc:oracle")) {
            driverClassName = "oracle.jdbc.driver.OracleDriver";
        } else if (url.contains("jdbc:mysql")) {
            driverClassName = "com.mysql.jdbc.Driver";
        } else {
            return null;
        }
        String username = environment.getProperty("second.datasource.username");
        if (username == null) {username = "SYSDBA";}
        String password = environment.getProperty("second.datasource.password");
        if (password == null) {username = "masterkey";}
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return new JdbcTemplate(dataSource);
    }

}
