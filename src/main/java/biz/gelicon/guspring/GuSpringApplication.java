package biz.gelicon.guspring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@ConfigurationProperties
public class GuSpringApplication {

	private static final Logger logger = LoggerFactory.getLogger(GuSpringApplication.class);

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {

		logger.info("GuSpringApplication.main...");

		SpringApplication.run(GuSpringApplication.class, args);

		logger.info("GuSpringApplication.main...Ok");

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext ac) {
		applicationContext = ac;
	}

}
