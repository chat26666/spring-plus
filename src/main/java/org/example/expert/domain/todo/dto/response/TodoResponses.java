package org.example.expert.domain.todo.dto.response;

import java.time.LocalDateTime;

import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.Getter;

@Getter
public class TodoResponses {

	private final Long id;
	private final String title;
	private final String contents;
	private final String weather;
	private final UserResponse user;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final Long commentCount;

	public TodoResponses(Long id, String title, String contents, String weather, Long uid, String email, String nickname, LocalDateTime createdAt, LocalDateTime modifiedAt, Long commentCount) {
		this.id = id;
		this.title = title;
		this.contents = contents;
		this.weather = weather;
		this.user = new UserResponse(uid, email, nickname);
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.commentCount = commentCount;
	}
}
