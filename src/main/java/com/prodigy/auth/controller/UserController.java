package com.prodigy.auth.controller;

import com.prodigy.auth.dto.ApiResponse;
import com.prodigy.auth.entity.Utilisateur;
import com.prodigy.auth.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UtilisateurRepository utilisateurRepository;

    // ======================
    // PROFIL UTILISATEUR (Route protégée)
    // ======================
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", utilisateur.getId());
        profile.put("nom", utilisateur.getNom());
        profile.put("prenom", utilisateur.getPrenom());
        profile.put("email", utilisateur.getEmail());
        profile.put("telephone", utilisateur.getTelephone());
        profile.put("role", utilisateur.getRole().name());
        profile.put("photoProfil", utilisateur.getPhotoProfil());
        profile.put("createdAt", utilisateur.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.success("Profil récupéré avec succès", profile));
    }

    // ======================
    // ROUTE ADMIN UNIQUEMENT
    // ======================
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Liste des utilisateurs",
                        utilisateurRepository.findAll().stream().map(u -> {
                            Map<String, Object> user = new HashMap<>();
                            user.put("id", u.getId());
                            user.put("nom", u.getNomComplet());
                            user.put("email", u.getEmail());
                            user.put("role", u.getRole().name());
                            user.put("actif", u.getActif());
                            user.put("createdAt", u.getCreatedAt());
                            return user;
                        }).toList()
                )
        );
    }

    // ======================
    // ROUTE MODÉRATEUR
    // ======================
    @GetMapping("/moderator/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse> moderatorDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("message", "Bienvenue sur le tableau de bord modérateur");

        return ResponseEntity.ok(ApiResponse.success("Dashboard modérateur", stats));
    }
}
