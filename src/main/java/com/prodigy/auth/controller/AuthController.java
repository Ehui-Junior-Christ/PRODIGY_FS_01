package com.prodigy.auth.controller;

import com.prodigy.auth.dto.ApiResponse;
import com.prodigy.auth.dto.LoginDto;
import com.prodigy.auth.dto.LoginResponseDto;
import com.prodigy.auth.dto.RegisterDto;
import com.prodigy.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    // ======================
    // INSCRIPTION
    // ======================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Compte créé avec succès ! Vous pouvez maintenant vous connecter."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de la création du compte : " + e.getMessage()));
        }
    }

    // ======================
    // CONNEXION
    // ======================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginDto request) {
        try {
            LoginResponseDto response = authService.login(request.getEmail(), request.getMotDePasse());
            return ResponseEntity.ok(ApiResponse.success("Connexion réussie", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de la connexion"));
        }
    }

    // ======================
    // VÉRIFICATION TOKEN
    // ======================
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyToken() {
        // Si cette route est atteinte, le token JWT est valide (filtre déjà passé)
        return ResponseEntity.ok(ApiResponse.success("Token valide"));
    }
}
