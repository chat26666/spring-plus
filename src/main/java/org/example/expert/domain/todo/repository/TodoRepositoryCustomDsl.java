package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoResponses;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TodoRepositoryCustomDsl {

	Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

	Page<TodoResponses> searchSchedules(String title, LocalDateTime startDate, LocalDateTime endDate, String nickname,
		Pageable page);
}
