package com.AtomIDTest.ForumEngine.controllers;

import com.AtomIDTest.ForumEngine.DTO.JwtAuthenticationResponse;
import com.AtomIDTest.ForumEngine.DTO.SignInRequest;
import com.AtomIDTest.ForumEngine.DTO.SignUpRequest;
import com.AtomIDTest.ForumEngine.services.AuthenticationService;
import com.AtomIDTest.ForumEngine.services.UserService;
import com.AtomIDTest.ForumEngine.util.InvalidTopicIdException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService service;
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
    @GetMapping("/get-admin")
    public ResponseEntity<String> getAdmin() {
        service.getAdmin();
        return ResponseEntity.ok("Admin set success");
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidTopicIdException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (UsernameNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(404));
    }

}

