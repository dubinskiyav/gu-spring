package biz.gelicon.guspring.config;

import biz.gelicon.guspring.GuSpringApplication;
import biz.gelicon.guspring.utils.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Запускается при запуске Spring и инициализирует все что надо
 */
@Component
public class InitApp implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(InitApp.class);

    public void run(ApplicationArguments args) {
        logger.info("InitApp running...");
        GuSpringApplication.setApplicationContext(applicationContext);
        // Установим тип СУБД
        DatabaseUtils.setDbType(jdbcTemplate);
        // Считаем все аннотации @Table
        logger.info("InitApp running...Ok");
        test1();
    }

    /**
     * Для легких тестов во время разработки
     */
    private void test1() {
        if (true) {return;}
        logger.info("test1...");
        logger.info("test1...Ok");
    }
}