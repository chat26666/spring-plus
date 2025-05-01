package org.example.expert.domain.todo.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoResponses;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

	private final TodoService todoService;

	@PostMapping("/todos")
	public ResponseEntity<TodoSaveResponse> saveTodo(
		@Auth AuthUser authUser,
		@Valid @RequestBody TodoSaveRequest todoSaveRequest
	) {
		return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
	}

	@GetMapping("/todos")
	public ResponseEntity<Page<TodoResponse>> getTodos(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String weather,
		@RequestParam(required = false) @DateTimeFormat LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat LocalDate endDate
	) {
		return ResponseEntity.ok(todoService.getTodos(page, size, weather, startDate, endDate));
	}

	@GetMapping("/todos/{todoId}")
	public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
		return ResponseEntity.ok(todoService.getTodo(todoId));
	}

	// 경로를 todos 를 쓰고싶은데 비슷한 api 가 이미 해당 url 을 쓰고 있어서 임시로 이걸로 대체합니다
	@GetMapping("/todos/responses")
	public ResponseEntity<Page<TodoResponses>> getTodoResponses(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String title,
		@RequestParam(required = false) String nickname,
		@RequestParam(required = false) @DateTimeFormat LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat LocalDate endDate
	) {
		return ResponseEntity.ok(todoService.getTodoResponses(page, size, title, nickname, startDate, endDate));
	}
}
