package org.example.expert.domain.user.controller;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PutMapping("/users/profile")
    public void updateProfile(@Auth AuthUser authUser, @RequestParam MultipartFile image) {
        userService.updateProfile(1, image);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<Map<String, String>> getProfile(@Auth AuthUser authUser) {
        return ResponseEntity.ok(userService.getProfile(authUser.getId()));
    }
}
