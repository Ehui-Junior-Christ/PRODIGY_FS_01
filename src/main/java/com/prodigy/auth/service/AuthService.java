package com.prodigy.auth.service;

import com.prodigy.auth.dto.LoginResponseDto;
import com.prodigy.auth.dto.RegisterDto;
import com.prodigy.auth.entity.Role;
import com.prodigy.auth.entity.Utilisateur;
import com.prodigy.auth.repository.UtilisateurRepository;
import com.prodigy.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ======================
    // INSCRIPTION
    // ======================
    public void register(RegisterDto request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // Vérifier si le téléphone existe déjà
        if (request.getTelephone() != null && !request.getTelephone().isBlank()) {
            if (utilisateurRepository.existsByTelephone(request.getTelephone().trim())) {
                throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
            }
        }

        // Déterminer le rôle
        Role role = Role.USER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rôle invalide : " + request.getRole());
            }
        }

        // Créer l'utilisateur avec le mot de passe haché
        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom().trim())
                .prenom(request.getPrenom().trim())
                .email(request.getEmail().trim().toLowerCase())
                .telephone(request.getTelephone() != null ? request.getTelephone().trim() : null)
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(role)
                .actif(true)
                .build();

        utilisateurRepository.save(utilisateur);
    }

    // ======================
    // CONNEXION
    // ======================
    public LoginResponseDto login(String email, String motDePasse) {
        // Authentification via Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.trim().toLowerCase(), motDePasse)
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifier si le compte est actif
        if (!utilisateur.getActif()) {
            throw new RuntimeException("Compte désactivé. Contactez l'administrateur.");
        }

        // Générer le token JWT avec le rôle en claim
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", utilisateur.getRole().name());
        extraClaims.put("nom", utilisateur.getNomComplet());
        extraClaims.put("userId", utilisateur.getId());

        UserDetails userDetails = new User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
                )
        );

        String token = jwtService.generateToken(extraClaims, userDetails);

        // Construire la réponse
        return LoginResponseDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .role(utilisateur.getRole().name())
                .photoProfil(utilisateur.getPhotoProfil())
                .token(token)
                .tokenType("Bearer")
                .build();
    }
}
