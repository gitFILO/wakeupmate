package com.example.wakeupmate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WakeupmateApplicationTests {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("데이터베이스에 연결할 수 있다")
	@Transactional
	public void testJdbcConnection() {
		//given
		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_table (id INT, name VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO test_table VALUES (1, 'Kim')");

		//when
		String result = jdbcTemplate.queryForObject("SELECT name FROM test_table WHERE id = 1", String.class);

		//then
		assertTrue("Kim".equals(result));
	}

}
