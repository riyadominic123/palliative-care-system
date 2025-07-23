package com.erfan.cch.Security;

import com.erfan.cch.Models.User;
import com.erfan.cch.Repo.UserRepository;
import com.erfan.cch.Security.Dto.AuthenticationRequest;
import com.erfan.cch.Security.Dto.AuthenticationResponse;
import com.erfan.cch.Security.Dto.ExtractEmailDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public User registerUser(User registrationDto) {
        try {
            User user = new User();
            user.setEmail(registrationDto.getEmail());
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setUserType(registrationDto.getUserType());
            user.setName(registrationDto.getName());
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists: " + registrationDto.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during registration.", e);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    public Boolean validateToken(String jwtFromRequest) {
      return  jwtService.isTokenExpired(jwtFromRequest);
    }

    public ResponseEntity<Long> getIdFromToken(HttpServletRequest request) {
        String jwtUserId = jwtService.extractId(jwtUtils.getJwtFromRequest(request));
        Long response = Long.valueOf(jwtUserId);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> getEmailFromToken(HttpServletRequest request) {
        String jwtUserEmail = jwtService.extractUsername(jwtUtils.getJwtFromRequest(request));

        return ResponseEntity.ok(jwtUserEmail);
    }

    public ResponseEntity<String> getEmailFromTokenUsingBody(ExtractEmailDto extractEmailDto) {
        String jwtUserEmail = jwtService.extractUsername(extractEmailDto.token);
        return ResponseEntity.ok(jwtUserEmail);

    }

    public ResponseEntity<Long> getIdFromTokenUsingBody(ExtractEmailDto extractEmailDto) {
        String jwtUserId = jwtService.extractId(extractEmailDto.token);
        return ResponseEntity.ok(Long.valueOf(jwtUserId));
    }
}
