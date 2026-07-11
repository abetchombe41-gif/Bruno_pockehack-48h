package ca.cegep.pokedex.controleur;

import ca.cegep.pokedex.dao.PokemonDao;
import ca.cegep.pokedex.modele.Pokemon;
import ca.cegep.pokedex.service.PokemonApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Optional;

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

    @FXML private VBox conteneurFiche;

    private final PokemonApiService apiService = new PokemonApiService();
    private final PokemonDao pokemonDao = new PokemonDao();

    @FXML
    public void initialize() {
        // 1. Focus automatique au démarrage (Bonus +1 pt)
        Platform.runLater(() -> champRecherche.requestFocus());

        // 2. Écouteur de sélection pour le clic sur la liste
        listeCaptures.getSelectionModel().selectedItemProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            if (nouvelleValeur != null) {
                String[] parties = nouvelleValeur.split(" - ");
                if (parties.length > 1) {
                    chargerPokemonDepuisBaseDeDonnees(parties[1].trim());
                }
            }
        });

        // 3. EXIGENCE MVP : Chargement automatique au démarrage depuis la base de données (Thread d'arrière-plan)
        new Thread(() -> {
            try {
                List<String> pokemonsEnregistres = pokemonDao.recupererTousLesNoms();
                Platform.runLater(() -> listeCaptures.getItems().addAll(pokemonsEnregistres));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void actionRechercher() {
        String recherche = champRecherche.getText().trim();
        if (recherche.isEmpty()) return;

        boutonRecherche.setDisable(true);

        new Thread(() -> {
            try {
                Pokemon pokemon = apiService.chercherPokemon(recherche);
                pokemonDao.sauvegarderOuMettreAJour(pokemon);

                Platform.runLater(() -> {
                    mettreAJourInterface(pokemon);
                    boutonRecherche.setDisable(false);
                    champRecherche.clear();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    afficherAlerteErreur("Erreur de recherche", e.getMessage());
                    boutonRecherche.setDisable(false);
                });
            }
        }).start();
    }

    private void chargerPokemonDepuisBaseDeDonnees(String nom) {
        new Thread(() -> {
            try {
                Pokemon pokemonEnCache = pokemonDao.recupererParNom(nom);
                if (pokemonEnCache != null) {
                    Platform.runLater(() -> mettreAJourInterface(pokemonEnCache));
                }
            } catch (Exception e) {
                e.printStackTrace();
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

        // Styles dynamiques CSS
        conteneurFiche.getStyleClass().removeAll(
                "type-normal", "type-fire", "type-water", "type-electric", "type-grass",
                "type-ice", "type-fighting", "type-poison", "type-ground", "type-flying",
                "type-psychic", "type-bug", "type-rock", "type-ghost", "type-dragon",
                "type-dark", "type-steel", "type-fairy"
        );
        String styleType = "type-" + p.typePrincipal().toLowerCase();
        conteneurFiche.getStyleClass().add(styleType);

        // Statistiques
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

    /**
     * EXIGENCE MVP : Suppression sécurisée avec boîte de dialogue de confirmation.
     */
    @FXML
    void actionSupprimer() {
        String selection = listeCaptures.getSelectionModel().getSelectedItem();
        if (selection != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Voulez-vous vraiment supprimer " + selection + " du Pokédex ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                String[] parties = selection.split(" - ");
                if (parties.length > 1) {
                    String nomPokemon = parties[1].trim();

                    new Thread(() -> {
                        try {
                            // Suppression physique en BD locale
                            pokemonDao.supprimerParNom(nomPokemon);
                            Platform.runLater(() -> {
                                listeCaptures.getItems().remove(selection);
                                // Réinitialisation de la fiche visuelle après suppression
                                labelNomPokemon.setText("Pokédex Ultime");
                                labelTypes.setText("Types: --");
                                imagePokemon.setImage(null);
                                barrePv.setProgress(0);
                                barreAttaque.setProgress(0);
                                barreDefense.setProgress(0);
                                barreAttaqueSp.setProgress(0);
                                barreDefenseSp.setProgress(0);
                                barreVitesse.setProgress(0);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
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
