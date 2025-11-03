package ru.mifiSoul.MultiBankApp.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mifiSoul.MultiBankApp.database.entity.Role;
import ru.mifiSoul.MultiBankApp.database.entity.RoleEntity;
import ru.mifiSoul.MultiBankApp.database.entity.UserEntity;
import ru.mifiSoul.MultiBankApp.database.repository.RoleRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.JwtAuthenticationResponse;
import ru.mifiSoul.MultiBankApp.dto.MessageResponse;
import ru.mifiSoul.MultiBankApp.dto.SignInRequest;
import ru.mifiSoul.MultiBankApp.dto.SignUpRequest;
import ru.mifiSoul.MultiBankApp.exception.BadRequestException;
import ru.mifiSoul.MultiBankApp.infrastructure.config.jwt.JwtUtils;
import ru.mifiSoul.MultiBankApp.service.UserDetailsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> authUser(@Valid @RequestBody SignInRequest signInRequest) {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            signInRequest.getLogin(),
                            signInRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt,
                    userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), userDetails.getPhone(), roles));
    }

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException("Phone already exists");
        }

        UserEntity user = new UserEntity(signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPhone(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> reqRoles = signUpRequest.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if (reqRoles == null || reqRoles.isEmpty()) {
            RoleEntity userRole = roleRepository
                    .findByRole(Role.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role USER not found"));
            roles.add(userRole);
        } else {
            reqRoles.forEach(roleName -> {
                if (roleName.equalsIgnoreCase("user")) {
                    RoleEntity userRole = roleRepository
                            .findByRole(Role.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role USER not found"));
                    roles.add(userRole);
                }
            });

        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User CREATED"));
    }
}
