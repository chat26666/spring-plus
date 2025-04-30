package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomDslImpl implements TodoRepositoryCustomDsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {

		Todo todo = queryFactory
			.selectFrom(QTodo.todo)
			.leftJoin(QTodo.todo.user, QUser.user).fetchJoin()
			.where(QTodo.todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(todo);
	}
}
