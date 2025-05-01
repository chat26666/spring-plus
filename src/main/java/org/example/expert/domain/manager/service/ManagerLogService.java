package org.example.expert.domain.manager.service;
import org.example.expert.domain.manager.entity.ManagerLog;
import org.example.expert.domain.manager.repository.ManagerLogRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerLogService {

	private final ManagerLogRepository managerLogRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveManagerLog(User user, String action) {
		managerLogRepository.save(new ManagerLog(user, action));
	}

}
