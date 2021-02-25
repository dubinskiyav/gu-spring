package biz.gelicon.guspring.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
@Tag(name = "Конфигурация JDBC", description = "Создает JdbcTemplate к другим базам данных ")
public class SpringJdbcConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringJdbcConfig.class);

    @Bean
    public JdbcTemplate timewebJdbcTemplate () {
        logger.info("timewebJdbcTemplate");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://78.40.219.225:5432/capital");
        dataSource.setUsername("SYSDBA");
        dataSource.setPassword("masterkey");
        return new JdbcTemplate(dataSource);
    }

}
