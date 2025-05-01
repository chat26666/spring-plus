package org.example.expert.domain.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.expert.config.QueryDslConfig;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


//UserRepository 를 실제로 주입받음
@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@BeforeAll
	void setUp() {
		List<User> batch = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			String nick = String.valueOf(i);
			batch.add(new User("email" + i, "pwd", UserRole.USER, nick));
			if (batch.size() == 5000) {
				userRepository.saveAll(batch);
				batch.clear();
			}
		}
		if (!batch.isEmpty())
			userRepository.saveAll(batch);
	}

	@Test
	void findByNickNameTest() {
		String targetNickname = "450000";
		long startTime = System.nanoTime();
		Optional<User> user = userRepository.findByNickname(targetNickname);
		long endTime = System.nanoTime();
		long result = endTime - startTime;
		System.out.println("실행시간 : " + (result / 1000000));
		assertTrue(user.isPresent());
	}

}