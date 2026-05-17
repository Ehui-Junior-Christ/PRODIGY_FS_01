# PRODIGY_FS_01

Listed directory PRODIGY_FS_01
Viewed explication_projet.md:1-137

Voici une description complète et structurée, spécialement rédigée pour que tu puisses la copier-coller directement dans le fichier `README.md` de ton dépôt GitHub :

***

```markdown
# Prodigy Auth System 🔐

## 🎯 À propos du projet (À quoi ça sert ?)
**Prodigy Auth System** est une solution full-stack complète et sécurisée de gestion d'authentification. Ce projet sert de fondation solide pour gérer les utilisateurs et sécuriser l'accès aux ressources dans n'importe quelle application web moderne. 

Il permet aux utilisateurs de s'inscrire, de se connecter en toute sécurité et d'accéder à un tableau de bord protégé, tout en gérant les permissions via des rôles définis (Utilisateur, Modérateur, Administrateur). L'application repose sur une architecture client-serveur *stateless* (sans état) utilisant des **Tokens JWT** (JSON Web Tokens).

**Caractéristiques principales & Stack Technique :**
*   **Backend (API REST) :** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate, MySQL.
*   **Frontend (Interface Utilisateur) :** HTML5, CSS3, JavaScript Vanille. Design premium moderne orienté "Glassmorphism" avec mode sombre et animations fluides.
*   **Sécurité :** Hachage irréversible des mots de passe (BCrypt), sécurisation des endpoints, gestion des sessions par JWT, et contrôle d'accès basé sur les rôles (RBAC).

---

## 🚀 Comment l'utiliser

### 📋 Prérequis
Pour exécuter ce projet localement, vous devez avoir installé :
*   [Java Development Kit (JDK) 21](https://jdk.java.net/21/) ou supérieur.
*   Un serveur [MySQL](https://www.mysql.com/) en cours d'exécution.
*   *Note : Maven n'a pas besoin d'être installé globalement, le projet inclut l'outil `mvnw` (Maven Wrapper).*

### ⚙️ Installation et Configuration

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/Ehui-Junior-Christ/PRODIGY_FS_01.git
   cd PRODIGY_FS_01
   ```

2. **Configurer la base de données :**
   * Assurez-vous que votre serveur MySQL est lancé.
   * La base de données `prodigy_fs_01` sera créée automatiquement grâce à la configuration.
   * Ouvrez le fichier `src/main/resources/application.properties` (si nécessaire) pour vérifier ou modifier vos identifiants MySQL locaux :
     ```properties
     spring.datasource.username=root
     spring.datasource.password=votre_mot_de_passe_mysql
     ```

3. **Lancer l'application backend :**
   Exécutez le projet via le terminal situé à la racine du projet :
   * **Sur Windows :**
     ```cmd
     mvnw.cmd spring-boot:run
     ```
   * **Sur Mac/Linux :**
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Accéder à l'application :**
   Le frontend étant directement packagé et servi par Spring Boot, il n'y a pas de serveur front-end séparé à lancer !
   * Ouvrez votre navigateur web.
   * Allez à l'adresse : **`http://localhost:8080/`** (vous serez automatiquement redirigé vers la page `login.html`).

### 🧪 Tester l'application
1. Cliquez sur **"Créer un compte"** pour tester le flux d'inscription. Remarquez la barre dynamique qui évalue la force de votre mot de passe en temps réel.
2. Une fois inscrit, utilisez vos identifiants pour vous **connecter**.
3. Observez le stockage de votre Token JWT dans le `sessionStorage` du navigateur.
4. Accédez au **Tableau de bord sécurisé**. Si vous essayez d'y accéder sans être connecté ou avec un token invalide, le système bloquera l'accès et vous renverra au login.