package com.erfan.cch.Security;
import com.erfan.cch.Enums.UserType;
import com.erfan.cch.Models.User;
import com.erfan.cch.Security.Dto.AuthenticationRequest;
import com.erfan.cch.Security.Dto.AuthenticationResponse;
import com.erfan.cch.Security.Dto.ExtractEmailDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("auth")

public class AuthController {

    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationService authenticationService, HttpServletRequest request, JwtUtils jwtUtils) {
        this.authenticationService = authenticationService;
        this.request = request;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User registrationDto) {
        registrationDto.setUserType(UserType.ADMIN);
        User registeredUser = authenticationService.registerUser(registrationDto);
        String response = "User registered successfully";
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @GetMapping("/extractId")
    public ResponseEntity<Long> printId() {
        return authenticationService.getIdFromToken(request);
    }
    @PostMapping("/extractId")
    public ResponseEntity<Long> printIdUsingBody(@RequestBody ExtractEmailDto extractEmailDto) {
        return authenticationService.getIdFromTokenUsingBody(extractEmailDto);
    }
    @GetMapping("/extractEmail")
    public ResponseEntity<String> printEmail() {
        return authenticationService.getEmailFromToken(request);
    }
    @PostMapping("/extractEmail")
    public ResponseEntity<String> printEmailUsingBody(@RequestBody ExtractEmailDto extractEmailDto) {
        return authenticationService.getEmailFromTokenUsingBody(extractEmailDto);
    }

}
