package ca.cegep.pokedex.modele;

/**
 * Record Java 17 représentant les données d'un Pokémon.
 * Structure immuable adaptée pour le transport des données de l'API et de la BD.
 */
public record Pokemon(
        int apiId,
        String nom,
        String typePrincipal,
        String typeSecondaire,
        String urlImage,
        int pv,
        int attaque,
        int defense,
        int attaqueSpeciale,
        int defenseSpeciale,
        int vitesse
) {}

