package ca.cegep.pokedex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stagePrincipal) throws Exception {
        // Récupération de la vue FXML depuis le répertoire resources
        FXMLLoader chargeur = new FXMLLoader(getClass().getResource("/PokedexVue.fxml"));
        Parent racine = chargeur.load();

        Scene scene = new Scene(racine, 800, 600);

        stagePrincipal.setTitle("PokéHack 48h - Le Pokédex Ultime");
        stagePrincipal.setScene(scene);
        stagePrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
