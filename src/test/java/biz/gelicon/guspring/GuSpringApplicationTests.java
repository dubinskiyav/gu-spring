package biz.gelicon.guspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GuSpringApplicationTests {

	static Logger logger = LoggerFactory.getLogger(GuSpringApplicationTests.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ApplicationContext applicationContext;

	@Value("${reloadtestdata}")
	private Boolean reloadtestdata = false;

	// Перед каждым тестом
	@BeforeEach
	public void initTests() {
		logger.info("initTests");
		GuSpringApplication.setApplicationContext(applicationContext);
	}

}
