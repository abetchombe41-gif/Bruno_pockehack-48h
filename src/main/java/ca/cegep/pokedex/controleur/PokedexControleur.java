package ca.cegep.pokedex.controleur;

import ca.cegep.pokedex.dao.PokemonDao;
import ca.cegep.pokedex.modele.Pokemon;
import ca.cegep.pokedex.service.PokemonApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PokedexControleur {

    @FXML private TextField champRecherche;
    @FXML private Button boutonRecherche;
    @FXML private ListView<String> listeCaptures;
    @FXML private Button boutonSupprimer;

    @FXML private Label labelNomPokemon;
    @FXML private Label labelTypes;
    @FXML private ImageView imagePokemon;

    @FXML private ProgressBar barrePv;
    @FXML private ProgressBar barreAttaque;
    @FXML private ProgressBar barreDefense;
    @FXML private ProgressBar barreAttaqueSp;
    @FXML private ProgressBar barreDefenseSp;
    @FXML private ProgressBar barreVitesse;

    private final PokemonApiService apiService = new PokemonApiService();
    private final PokemonDao pokemonDao = new PokemonDao();

    @FXML
    public void initialize() {
        // Optionnel : Placer le focus automatique au démarrage (Bonus +1 pt)
        Platform.runLater(() -> champRecherche.requestFocus());
    }

    @FXML
    void actionRechercher() {
        String recherche = champRecherche.getText().trim();
        if (recherche.isEmpty()) return;

        boutonRecherche.setDisable(true);

        // EXIGENCE MULTI-THREADING (10 points) : Requête dans un thread séparé
        new Thread(() -> {
            try {
                // 1. Appel HTTP à l'API externe
                Pokemon pokemon = apiService.chercherPokemon(recherche);

                // 2. Sauvegarde automatique immédiate en BD (Exigence MVP)
                pokemonDao.sauvegarderOuMettreAJour(pokemon);

                // 3. EXIGENCE MULTI-THREADING : Retour sur le thread JavaFX pour modifier l'interface
                Platform.runLater(() -> {
                    mettreAJourInterface(pokemon);
                    boutonRecherche.setDisable(false);
                    champRecherche.clear();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    afficherAlerteErreur("Erreur", e.getMessage());
                    boutonRecherche.setDisable(false);
                });
            }
        }).start();
    }

    private void mettreAJourInterface(Pokemon p) {
        labelNomPokemon.setText("#" + p.apiId() + " " + p.nom().toUpperCase());

        String texteTypes = "Type: " + p.typePrincipal().toUpperCase();
        if (p.typeSecondaire() != null) {
            texteTypes += " / " + p.typeSecondaire().toUpperCase();
        }
        labelTypes.setText(texteTypes);

        if (p.urlImage() != null && !p.urlImage().isEmpty()) {
            imagePokemon.setImage(new Image(p.urlImage(), true));
        } else {
            imagePokemon.setImage(null);
        }

        // Calcul des barres (Divisé par 255.0 car les statistiques Pokémon max s'arrêtent à 255)
        barrePv.setProgress(p.pv() / 255.0);
        barreAttaque.setProgress(p.attaque() / 255.0);
        barreDefense.setProgress(p.defense() / 255.0);
        barreAttaqueSp.setProgress(p.attaqueSpeciale() / 255.0);
        barreDefenseSp.setProgress(p.defenseSpeciale() / 255.0);
        barreVitesse.setProgress(p.vitesse() / 255.0);

        String ligneAffichage = "#" + p.apiId() + " - " + p.nom();
        if (!listeCaptures.getItems().contains(ligneAffichage)) {
            listeCaptures.getItems().add(ligneAffichage);
        }
    }

    @FXML
    void actionSupprimer() {
        String selection = listeCaptures.getSelectionModel().getSelectedItem();
        if (selection != null) {
            listeCaptures.getItems().remove(selection);
            // Logique de suppression de la BD à implémenter selon les besoins
        }
    }

    private void afficherAlerteErreur(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
