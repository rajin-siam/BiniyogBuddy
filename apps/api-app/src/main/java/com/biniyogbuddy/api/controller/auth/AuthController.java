package com.biniyogbuddy.api.controller.auth;

import com.biniyogbuddy.auth.dto.AuthLoginRequest;
import com.biniyogbuddy.auth.dto.AuthRegisterRequest;
import com.biniyogbuddy.auth.dto.AuthResponse;
import com.biniyogbuddy.auth.dto.LogoutRequest;
import com.biniyogbuddy.auth.dto.RefreshTokenRequest;
import com.biniyogbuddy.auth.service.AuthService;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageResource messageResource;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        String message = messageResource.getMessage("auth.register.success");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        String message = messageResource.getMessage("auth.login.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refresh(request.refreshToken());
        String message = messageResource.getMessage("auth.refresh.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request, @Valid @RequestBody LogoutRequest logoutRequest) {
        String accessToken = request.getHeader("Authorization").substring(7);
        authService.logout(accessToken, logoutRequest.refreshToken());
        String message = messageResource.getMessage("auth.logout.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
