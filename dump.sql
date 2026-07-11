-- 1. Suppression de la table si elle existe (sécurité pour le professeur)
DROP TABLE IF EXISTS pokemon;

-- 2. Création de la structure de la table
CREATE TABLE pokemon (
                         id_interne SERIAL PRIMARY KEY,
                         api_id INTEGER UNIQUE NOT NULL,
                         nom VARCHAR(100) NOT NULL,
                         type_principal VARCHAR(50) NOT NULL,
                         type_secondaire VARCHAR(50),
                         url_image VARCHAR(500),
                         pv INTEGER NOT NULL,
                         attaque INTEGER NOT NULL,
                         defense INTEGER NOT NULL,
                         attaque_speciale INTEGER NOT NULL,
                         defense_speciale INTEGER NOT NULL,
                         vitesse INTEGER NOT NULL,
                         date_capture TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Données de test obligatoires (Demande explicite de l'image de l'évaluation)
INSERT INTO pokemon (api_id, nom, type_principal, type_secondaire, url_image, pv, attaque, defense, attaque_speciale, defense_speciale, vitesse)
VALUES
    (25, 'pikachu', 'electric', NULL, 'https://githubusercontent.com', 35, 55, 40, 50, 50, 90),
    (6, 'charizard', 'fire', 'flying', 'https://githubusercontent.com', 78, 84, 78, 109, 85, 100),
    (9, 'blastoise', 'water', NULL, 'https://githubusercontent.com', 79, 83, 100, 85, 105, 78);
