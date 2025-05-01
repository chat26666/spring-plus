package org.example.expert.config;

import java.io.IOException;
import java.util.List;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SecurityJwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	protected void doFilterInternal(
		@NotNull HttpServletRequest httpRequest, @NotNull HttpServletResponse httpResponse,
		@NotNull FilterChain chain) throws
		ServletException,
		IOException {

		String url = httpRequest.getRequestURI();

		if (url.startsWith("/auth") || url.startsWith("/actuator")) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		String bearerJwt = httpRequest.getHeader("Authorization");

		if (bearerJwt == null) {
			// 토큰이 없는 경우 400을 반환합니다.
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
			return;
		}

		String jwt = jwtUtil.substringToken(bearerJwt);

		try {
			// JWT 유효성 검사와 claims 추출
			Claims claims = jwtUtil.extractClaims(jwt);
			if (claims == null) {
				httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
				return;
			}

			UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

			String role = (String)claims.get("userRole");
			Long userId = Long.parseLong(claims.getSubject());
			String email = (String)claims.get("email");
			String nickname = (String)claims.get("nickname");

			AuthUser authUser = new AuthUser(userId, email, userRole, nickname);

			Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, jwt,
				List.of(new SimpleGrantedAuthority(role)));

			//SecurityContextHolder 에 인증된 Authentication 이 있어야 다른 필터들이 인증 스킵
			SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
			securityContext.setAuthentication(authentication);
			SecurityContextHolder.setContext(securityContext);

			httpRequest.setAttribute("userId", userId);
			httpRequest.setAttribute("email", email);
			httpRequest.setAttribute("userRole", role);
			httpRequest.setAttribute("nickname", nickname);

			if (url.startsWith("/admin")) {
				// 관리자 권한이 없는 경우 403을 반환합니다.
				if (!UserRole.ADMIN.equals(userRole)) {
					httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
					return;
				}
				chain.doFilter(httpRequest, httpResponse);
				return;
			}

			chain.doFilter(httpRequest, httpResponse);
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
		} catch (Exception e) {
			log.error("Internal server error", e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}
}
