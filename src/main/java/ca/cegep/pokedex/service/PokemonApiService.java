package ca.cegep.pokedex.service;

import ca.cegep.pokedex.modele.Pokemon;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PokemonApiService {
    private final HttpClient client;
    private final ObjectMapper mapper;

    public PokemonApiService() {
        // Configuration recommandée : Timeout de 5 secondes pour éviter le gel réseau
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Interroge la PokéAPI et extrait les nœuds JSON avec Jackson.
     */
    public Pokemon chercherPokemon(String nomOuId) throws Exception {
        String url = "https://pokeapi.co" + nomOuId.toLowerCase().trim();
        HttpRequest requete = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> reponse = client.send(requete, HttpResponse.BodyHandlers.ofString());

        // Gestion du code d'erreur 404 exigée par le MVP
        if (reponse.statusCode() == 404) {
            throw new IllegalArgumentException("Le Pokémon '" + nomOuId + "' n'existe pas dans la PokéAPI.");
        }

        JsonNode racine = mapper.readTree(reponse.body());

        // Extraction des attributs de base
        int id = racine.get("id").asInt();
        String nom = racine.get("name").asText();

        String urlImage = "";
        if (racine.has("sprites") && racine.get("sprites").has("other")
                && racine.get("sprites").get("other").has("official-artwork")) {
            urlImage = racine.get("sprites").get("other").get("official-artwork").get("front_default").asText();
        }

        // Extraction sécurisée des types (gestion du type secondaire optionnel)
        JsonNode noeudTypes = racine.get("types");
        String type1 = noeudTypes.get(0).get("type").get("name").asText();
        String type2 = noeudTypes.has(1) ? noeudTypes.get(1).get("type").get("name").asText() : null;

        // Extraction du tableau des 6 statistiques
        JsonNode noeudStats = racine.get("stats");
        int pv = noeudStats.get(0).get("base_stat").asInt();
        int att = noeudStats.get(1).get("base_stat").asInt();
        int def = noeudStats.get(2).get("base_stat").asInt();
        int attSp = noeudStats.get(3).get("base_stat").asInt();
        int defSp = noeudStats.get(4).get("base_stat").asInt();
        int vit = noeudStats.get(5).get("base_stat").asInt();

        return new Pokemon(id, nom, type1, type2, urlImage, pv, att, def, attSp, defSp, vit);
    }
}
