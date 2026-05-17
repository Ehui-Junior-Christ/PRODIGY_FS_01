# PRODIGY_FS_01

## Système d'Authentification Prodigy (Prodigy Auth System) 🔐## 🎯 À propos du projet
Prodigy Auth System est une solution full-stack complète et sécurisée dédiée à la gestion d'authentification des utilisateurs. Ce projet sert de fondation solide pour sécuriser l'accès aux ressources et gérer les comptes dans n'importe quelle application web moderne.
Il permet aux utilisateurs de s'inscrire, de se connecter en toute sécurité et d'accéder à un tableau de bord protégé. L'application intègre également une gestion des permissions via des rôles définis (Utilisateur, Modérateur, Administrateur). L'architecture repose sur un modèle client-serveur stateless (sans état) utilisant des Tokens JWT (JSON Web Tokens).
## 🛠️ Stack Technique & Caractéristiques principales

* Backend (API REST) : Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate, MySQL.
* Frontend (Interface Utilisateur) : HTML5, CSS3, JavaScript Vanille. Le design moderne adopte le style Glassmorphism avec un mode sombre natif et des animations fluides.
* Sécurité : Hachage irréversible des mots de passe avec BCrypt, sécurisation stricte des points d'accès (endpoints), gestion des sessions par JWT, et contrôle d'accès basé sur les rôles (RBAC).

## 🚀 Guide d'utilisation## 📋 Prérequis
Avant de lancer le projet localement, assurez-vous d'avoir installé :

* Java Development Kit (JDK) 21 ou une version supérieure.
* Un serveur MySQL actif.
* Note : Il n'est pas nécessaire d'installer Maven globalement, le projet inclut le script mvnw (Maven Wrapper).

## ⚙️ Configuration et Installation

   1. Cloner le projet :
   
   git clone https://github.com
   cd PRODIGY_FS_01
   
   2. Configurer la base de données :
   * Lancez votre serveur MySQL.
      * La base de données prodigy_fs_01 est configurée pour se créer automatiquement.
      * Si nécessaire, ajustez vos identifiants de connexion MySQL locaux dans le fichier src/main/resources/application.properties :
      
      spring.datasource.username=votre_utilisateur_mysql
      spring.datasource.password=votre_mot_de_passe_mysql
      
      3. Lancer le serveur backend :
   Ouvrez un terminal à la racine du projet et exécutez la commande correspondante à votre système :
   * Sur Windows :
      
      mvnw.cmd spring-boot:run
      
      * Sur Mac / Linux :
      
      ./mvnw spring-boot:run
      
      4. Accéder à l'application :
   Le frontend est directement intégré et servi par Spring Boot. Vous n'avez pas besoin de lancer de serveur web séparé.
   * Ouvrez votre navigateur internet.
      * Rendez-vous sur : http://localhost:8080/ (vous serez automatiquement redirigé vers la page de connexion).
   
## 🧪 Scénario de test recommandé

   1. Inscription : Cliquez sur "Créer un compte". Une barre dynamique analyse et évalue la force de votre mot de passe en temps réel.
   2. Connexion : Utilisez vos nouveaux identifiants pour vous connecter à l'application.
   3. Vérification du Token : Ouvrez les outils de développement de votre navigateur (F12) pour observer le stockage sécurisé du Token JWT dans le sessionStorage.
   4. Sécurité : Accédez au tableau de bord. Déconnectez-vous ou tentez d'y accéder directement sans token pour tester le blocage automatique et la redirection vers la page de connexion.

