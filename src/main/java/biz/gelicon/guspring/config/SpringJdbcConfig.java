package biz.gelicon.guspring.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
@Tag(name = "Конфигурация JDBC", description = "Создает JdbcTemplate к другим базам данных ")
public class SpringJdbcConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringJdbcConfig.class);

    @Autowired
    private Environment environment;

}
