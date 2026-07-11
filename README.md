# PokéHack 48h - Le Pokédex Ultime 🚀

Projet de mi-session réalisé en binôme(dans ce cas solo pour moi) dans le cadre du cours **420-930-MA (Session Été 2026)**. Cette application JavaFX est un Pokédex complet, fluide et asynchrone, connecté à la PokéAPI v2 et persistant grâce à une base de données locale PostgreSQL.

---

## 👥 L'Équipe
* **[Nom de l'équipier 1: Abé Tchombé Bruno Dimitri]

🔗 **Lien du dépôt GitHub :** `[https://github.com/abetchombe41-gif/Bruno_pockehack-48h]`

---

## 🛠️ Stack Technique & Prérequis
* **Langage :** Java 17+ (Utilisation des *Records* natifs)
* **Framework UI :** JavaFX 21 (Architecture MVC stricte)
* **Analyseur JSON :** Jackson Databind 2.16+ (`JsonNode`)
* **Base de données :** PostgreSQL 15+ (Pilote JDBC natif, sans ORM)
* **Gestionnaire de build :** Maven

---

## 🎯 Fonctionnalités implémentées (MVP Complété)
* **Recherche instantanée :** Recherche par nom ou par identifiant numérique (ID) directement connectée à la PokéAPI v2.
* **Multi-threading rigoureux :** Isolation complète des requêtes HTTP distantes dans un `Thread` d'arrière-plan dédié pour éviter tout gel de l'interface utilisateur. Mises à jour de la vue synchronisées via `Platform.runLater()`.
* **Persistance automatique (UPSERT) :** Sauvegarde immédiate en base de données locale lors de chaque nouvelle recherche réussie grâce à une clause SQL `ON CONFLICT (api_id) DO UPDATE`.
* **Cache local intelligent :** Chargement automatique de la liste complète des captures au démarrage de l'application et rafraîchissement instantané de la fiche lors d'un clic sur la liste sans surcharger l'API publique (respect de la limite des 100 requêtes/min).
* **Gestion des erreurs :** Interception et affichage propre des exceptions (Pokémon 404 introuvable, absence de connexion réseau, serveur SQL inaccessible).
* **Sécurisation des données :** Action de suppression de la liste et de la base de données sécurisée par une boîte de dialogue de confirmation graphique (`Alert.AlertType.CONFIRMATION`).

---

## 🎨 UX & Design personnalisé (20 points)
* **Interface responsive :** Agencement soigné à l'aide de conteneurs de placement (`BorderPane`, `VBox`, `GridPane`).
* **Feuille de style CSS thématique :** Intégration des couleurs et dégradés officiels pour les 18 types de Pokémon. L'arrière-plan de la carte d'information s'adapte dynamiquement selon le type principal du Pokémon affiché.
* **Composants soignés :** Représentation visuelle des 6 statistiques de base (PV, Attaque, Défense, Att. Spé., Déf. Spé., Vitesse) à l'aide de composants `ProgressBar` JavaFX gradués.

---

## ⭐ Fonctionnalités Bonus intégrées
* **Focus automatique :** Placement automatique du curseur textuel dans le champ de recherche dès l'initialisation de l'application.

---

## 🚀 Instructions de configuration et d'exécution

### 1. Configuration de la base de données
1. Ouvrez **pgAdmin 4** et créez une base de données nommée exactement `pockehack_db`.
2. Ouvrez l'outil de requête (*Query Tool*) sur cette base et exécutez le script SQL fourni dans le fichier `schema.sql` (ou le dossier `dump`) pour générer la table `pokemon`.
3. Vérifiez et ajustez vos identifiants de connexion (URL, utilisateur et mot de passe) aux lignes 12-14 du fichier `src/main/java/ca/cegep/pokedex/dao/PokemonDao.java`.

### 2. Lancement de l'application
Le projet intègre une classe utilitaire `Lanceur` qui permet d'exécuter l'application de façon stable dans IntelliJ sans nécessiter de configuration manuelle de *VM Options*.
1. Ouvrez le projet dans **IntelliJ IDEA**.
2. Synchronisez les dépendances Maven si nécessaire.
3. Ouvrez le fichier `src/main/java/ca/cegep/pokedex/Lanceur.java`.
4. Faites un clic droit sur le code et sélectionnez **Run 'Lanceur.main()'**.
