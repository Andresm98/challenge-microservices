package com.anax.account;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest(properties = {
		"account.security.username=test-user",
		"account.security.password=test-password"
})
class AccountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
