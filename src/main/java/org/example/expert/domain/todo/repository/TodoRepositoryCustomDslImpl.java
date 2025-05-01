package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.todo.dto.response.TodoResponses;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TodoRepositoryCustomDslImpl extends QuerydslRepositorySupport implements TodoRepositoryCustomDsl {

	private final JPAQueryFactory queryFactory;

	public TodoRepositoryCustomDslImpl(JPAQueryFactory queryFactory) {

		super(Todo.class);
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {

		Todo todo = queryFactory
			.selectFrom(QTodo.todo)
			.leftJoin(QTodo.todo.user, QUser.user).fetchJoin()
			.where(QTodo.todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(todo);
	}

	@Override
	public Page<TodoResponses> searchSchedules(String title, LocalDateTime startDate, LocalDateTime endDate,
		String nickname, Pageable page) {

		QTodo t = QTodo.todo;
		QComment c = QComment.comment;
		QUser u = QUser.user;

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (title != null)
			booleanBuilder.and(t.title.like("%" + title + "%"));

		if (startDate != null)
			booleanBuilder.and(t.createdAt.after(startDate));

		if (endDate != null)
			booleanBuilder.and(t.createdAt.before(endDate));

		if (nickname != null)
			booleanBuilder.and(t.user.nickname.like("%" + nickname + "%"));

		JPQLQuery<TodoResponses> query = queryFactory
			.select(Projections.constructor(TodoResponses.class, t.id, t.title, t.contents, t.weather, t.user.id,
				t.user.email, t.user.nickname, t.createdAt, t.modifiedAt, c.id.count()))
			.from(t)
			.innerJoin(t.user, u)
			.leftJoin(t.comments, c)
			.where(booleanBuilder)
			.groupBy(t.id)
			.orderBy(t.createdAt.desc());

		JPQLQuery<TodoResponses> paged = getQuerydsl().applyPagination(page, query);
		long total = paged.fetchCount();
		List<TodoResponses> list = paged.fetch();

		return new PageImpl<>(list, page, total);
	}
}
