package org.example.auth;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.LoginResponse;
import org.example.configurations.JwtUtil;
import org.example.user.User;
import org.example.user.UserService;
import org.example.user.dto.UserCreateRequest;
import org.example.user.dto.UserLoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody UserLoginRequest request) {
            User user = userService.getUserByEmail(request.email());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
            UUID userId = user.getId();
            String token = jwtUtil.generateToken(userDetails, userId);
            return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
