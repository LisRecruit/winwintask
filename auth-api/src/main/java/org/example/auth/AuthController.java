package org.example;

import lombok.RequiredArgsConstructor;
import org.example.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/login")
    
    @PostMapping("/register")
}
