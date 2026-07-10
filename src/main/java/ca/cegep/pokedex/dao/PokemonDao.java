package ca.cegep.pokedex.dao;

import ca.cegep.pokedex.modele.Pokemon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PokemonDao {
    private final String url = "jdbc:postgresql://localhost:5432/pockehack_db";
    private final String utilisateur = "postgres";
    private final String motDePasse = "12345*"; // Ajustez si votre mot de passe pgAdmin est différent

    /**
     * Insère un Pokémon ou le met à jour s'il existe déjà (UPSERT).
     */
    public void sauvegarderOuMettreAJour(Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO pokemon (api_id, nom, type_principal, type_secondaire, url_image, pv, attaque, defense, attaque_speciale, defense_speciale, vitesse)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (api_id) 
            DO UPDATE SET nom = EXCLUDED.nom, 
                          type_principal = EXCLUDED.type_principal, 
                          type_secondaire = EXCLUDED.type_secondaire, 
                          url_image = EXCLUDED.url_image,
                          pv = EXCLUDED.pv,
                          attaque = EXCLUDED.attaque,
                          defense = EXCLUDED.defense,
                          attaque_speciale = EXCLUDED.attaque_speciale,
                          defense_speciale = EXCLUDED.defense_speciale,
                          vitesse = EXCLUDED.vitesse,
                          date_capture = CURRENT_TIMESTAMP;
        """;

        try (Connection con = DriverManager.getConnection(url, utilisateur, motDePasse);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pokemon.apiId());
            ps.setString(2, pokemon.nom());
            ps.setString(3, pokemon.typePrincipal());
            ps.setString(4, pokemon.typeSecondaire());
            ps.setString(5, pokemon.urlImage());
            ps.setInt(6, pokemon.pv());
            ps.setInt(7, pokemon.attaque());
            ps.setInt(8, pokemon.defense());
            ps.setInt(9, pokemon.attaqueSpeciale());
            ps.setInt(10, pokemon.defenseSpeciale());
            ps.setInt(11, pokemon.vitesse());

            ps.executeUpdate();
        }
    }

    /**
     * Récupère un Pokémon depuis PostgreSQL par son nom.
     */
    public Pokemon recupererParNom(String nom) throws Exception {
        String sql = "SELECT * FROM pokemon WHERE nom = ?;";

        try (Connection con = DriverManager.getConnection(url, utilisateur, motDePasse);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nom.toLowerCase().trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pokemon(
                            rs.getInt("api_id"),
                            rs.getString("nom"),
                            rs.getString("type_principal"),
                            rs.getString("type_secondaire"),
                            rs.getString("url_image"),
                            rs.getInt("pv"),
                            rs.getInt("attaque"),
                            rs.getInt("defense"),
                            rs.getInt("attaque_speciale"),
                            rs.getInt("defense_speciale"),
                            rs.getInt("vitesse")
                    );
                }
            }
        }
        return null;
    }
}
