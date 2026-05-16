# Documentation du Système d'Authentification Prodigy Auth

Ce document détaille l'implémentation complète du système d'authentification sécurisé que nous avons mis en place. Le projet est divisé en deux parties : un backend robuste développé en **Spring Boot** et une interface frontend moderne en **HTML/CSS/JS vanille**.

---

## 1. Architecture Globale

L'application suit une architecture client-serveur stateless (sans état de session côté serveur) :
1. Le client (navigateur) envoie ses identifiants au serveur.
2. Le serveur (Spring Boot) vérifie, hache le mot de passe, et génère un **Token JWT (JSON Web Token)**.
3. Le client stocke ce token dans le `sessionStorage` et l'envoie dans l'en-tête (Header) `Authorization` de chaque requête ultérieure.
4. Le serveur vérifie le token à chaque requête pour autoriser ou refuser l'accès aux ressources protégées.

---

## 2. Le Backend (Spring Boot)

Le backend utilise Java 21, Spring Boot 3.5.x, Spring Security, Spring Data JPA, Hibernate, MySQL et JJWT pour la gestion des tokens.

### 2.1. Configuration
**`pom.xml`**
Contient toutes les dépendances du projet :
- `spring-boot-starter-web` : Pour créer des API REST (Contrôleurs).
- `spring-boot-starter-data-jpa` & `mysql-connector-j` : Pour la connexion à la base de données MySQL et l'ORM Hibernate.
- `spring-boot-starter-security` : Pour sécuriser les routes et gérer l'authentification.
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` : Librairie pour générer et lire les tokens JWT.
- `lombok` : Pour générer automatiquement les getters, setters et constructeurs.

**`application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/prodigy_fs_01?...
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=ProdigyFS01SecretKeyForJWTAuthentication...
app.jwt.expiration-ms=86400000
```
Définit la connexion à la base de données (création automatique avec `update`), la configuration du port (8080), l'emplacement des fichiers statiques frontend et les clés secrètes pour signer le token JWT (avec une expiration de 24h).

### 2.2. Modèle de Données (Entities & Repositories)
**`Role.java` (Enum)**
Définit les rôles possibles pour le contrôle d'accès : `USER`, `MODERATOR`, `ADMIN`.

**`Utilisateur.java` (Entity)**
La classe qui représente la table `utilisateurs` en base de données. 
- Utilise `@Entity` pour dire à Hibernate de créer la table.
- Les contraintes `@NotBlank` et `@Email` assurent la validation des données.
- Le mot de passe est stocké sous forme hachée.
- Les méthodes `@PrePersist` et `@PreUpdate` mettent à jour automatiquement les dates de création/modification.

**`UtilisateurRepository.java`**
Interface qui hérite de `JpaRepository`. Elle fournit les requêtes SQL classiques (`save`, `findById`) et nous avons ajouté des méthodes personnalisées utiles : `findByEmail`, `existsByEmail`, `existsByTelephone`.

### 2.3. Sécurité (Spring Security & JWT)
C'est le cœur du système.

**`JwtService.java`**
Gère tout ce qui concerne le token :
- `generateToken(...)` : Crée le token avec l'email, le rôle et le signe avec la clé secrète (algorithme HMAC-SHA256).
- `extractUsername(...)` et `isTokenValid(...)` : Lisent le token fourni par le client et vérifient qu'il n'est pas expiré ni falsifié.

**`CustomUserDetailsService.java`**
Implémente une interface de Spring Security. Sa seule mission est de charger un utilisateur depuis la base de données (via son email) et de le convertir en un objet `UserDetails` compréhensible par Spring Security (incluant ses rôles sous forme de `GrantedAuthority`).

**`JwtAuthenticationFilter.java`**
Ce filtre s'exécute **avant chaque requête HTTP** :
1. Il lit l'en-tête `Authorization: Bearer <token>`.
2. S'il y a un token, il l'extrait et vérifie sa validité avec `JwtService`.
3. S'il est valide, il place l'utilisateur dans le `SecurityContextHolder`, ce qui indique à Spring que la requête est authentifiée.

**`SecurityConfig.java`**
La configuration centrale :
- Désactive le CSRF (car nous utilisons des tokens JWT, immunisés contre les attaques CSRF classiques).
- Rend la gestion de session "STATELESS" (sans état, le serveur ne stocke pas de session).
- Configure le CORS pour autoriser le frontend à appeler l'API.
- Définit les routes publiques (`/api/auth/**`, `/login.html`, etc.) et les routes protégées (`/api/admin/**` nécessite ADMIN).
- Définit le `BCryptPasswordEncoder` pour hacher les mots de passe de manière irréversible.

### 2.4. Logique Métier (Services)
**`AuthService.java`**
Contient la logique d'inscription et de connexion :
- **register** : Vérifie si l'email existe. S'il n'existe pas, hache le mot de passe brut reçu du client avec `passwordEncoder.encode()` et sauvegarde l'utilisateur.
- **login** : Utilise `AuthenticationManager` de Spring pour vérifier l'email et le mot de passe. Si correct, génère un JWT via `JwtService` et renvoie un DTO contenant le token et les infos de l'utilisateur.

### 2.5. Contrôleurs (Endpoints REST)
**`AuthController.java`**
Expose les routes publiques :
- `POST /api/auth/register` : Appelle le service d'inscription.
- `POST /api/auth/login` : Appelle le service de connexion et renvoie le JWT.
- `GET /api/auth/verify` : Une route simple pour que le frontend vérifie si son token est toujours valide.

**`UserController.java`**
Expose les routes protégées :
- `GET /api/user/profile` : Accessible par n'importe quel utilisateur connecté. Récupère l'utilisateur depuis le `SecurityContextHolder`.
- `GET /api/user/admin/users` : Protégé par `@PreAuthorize("hasRole('ADMIN')")`. Seul un admin peut voir la liste.

---

## 3. Le Frontend (HTML, CSS, JS)

Le frontend a été conçu pour offrir une expérience premium ("glassmorphism", mode sombre, animations).

### 3.1. Structure CSS (`style.css`)
- **Variables CSS (`:root`)** : Centralisation des couleurs (violet primaire, fond sombre, texte clair) pour garantir la cohérence.
- **Background animé** : Des orbes colorés (`.orb`) avec un effet de flou (`filter: blur`) s'animent en arrière-plan pour un rendu dynamique.
- **Glassmorphism** : Utilisation de `background: rgba(..., 0.7)` et `backdrop-filter: blur(20px)` sur les conteneurs (formulaires, cartes du dashboard) pour donner cet effet de verre dépoli moderne.
- **Boutons premium** : Effet de survol avec un reflet qui traverse le bouton (via `::before` et `left: 100%`) et des ombres portées douces.

### 3.2. Les Pages HTML
**`index.html`**
Fichier racine qui redirige immédiatement vers la page de connexion.

**`login.html`** (Page de connexion)
- Contient une partie de "Branding" à gauche et le formulaire à droite.
- Les champs requièrent un email et un mot de passe.
- Possède un petit script JS intégré pour intercepter la soumission (submit), envoyer la requête POST à `/api/auth/login`, stocker le token retourné dans `sessionStorage`, puis rediriger vers le dashboard.

**`register.html`** (Page d'inscription)
- Propose des champs complets (Nom, Prénom, Email, Mot de passe, Confirmation, et Rôle).
- Intègre une **barre de force du mot de passe** (script JS) qui évalue la longueur et la complexité du mot de passe en temps réel, affichant une couleur et un texte (Faible, Bon, Excellent).
- Vérifie que le mot de passe et sa confirmation correspondent avant d'envoyer la requête POST à `/api/auth/register`.

**`dashboard.html`** (Tableau de bord protégé)
- L'espace utilisateur. Un script JS s'exécute dès le chargement : il vérifie la présence du token dans `sessionStorage` et effectue un appel à `/api/auth/verify`. Si le token est invalide ou absent, l'utilisateur est violemment redirigé vers `login.html`.
- Si valide, la page met à jour le DOM avec les informations stockées (Nom, Rôle, Initiales pour l'avatar).
- Le design est organisé sous forme de "cartes" (statistiques) et liste d'informations.

### 3.3. Logique partagée (`auth.js`)
Ce fichier rassemble les fonctions utilitaires :
- `showAlert(message, type)` : Affiche joliment une notification de succès ou d'erreur sur les formulaires, qui disparaît après 5 secondes.
- `togglePassword()` : Change l'attribut `type="password"` en `type="text"` pour révéler le mot de passe.
- `logout()` : Efface le `sessionStorage` (supprimant ainsi le token) et redirige vers le login.

---

## Conclusion
Le système mis en place est un standard de l'industrie (JWT + Spring Security + BCrypt). Il est hautement sécurisé (le mot de passe n'est jamais stocké en clair, les sessions sont infalsifiables grâce à la signature JWT) et l'interface offre un rendu visuel attractif et professionnel.
