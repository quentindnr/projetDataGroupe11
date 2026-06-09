package ihm_graphique;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import systeme_reconnaissance.PGMLoader;
import systeme_reconnaissance.Personne;
import systeme_reconnaissance.ResultatIdentification;
import systeme_reconnaissance.SystemeReconnaissance;

public class IhmGraphique extends Application {

    private Label texte;
    private ImageView imageView;
    private Label resultatLabel;
    private systeme_reconnaissance.SystemeReconnaissance systeme;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        /*
         * ===== TOP =====
         */
        Label title = new Label("Reconnaissance Faciale");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox top = new VBox(title);
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15;");

        root.setTop(top);

        /*
         * ===== IMAGE CARD =====
         */
        VBox imageBox = new VBox(10);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle("-fx-background-color: white;" + "-fx-padding: 20;" + "-fx-background-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 4);");

        Label importLabel = new Label("Import Image");
        importLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        texte = new Label("Drag & Drop ou clic");
        texte.setStyle("-fx-font-size: 14px;");

        imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        imageBox.getChildren().addAll(importLabel, texte, imageView);

        /*
         * ===== RESULT CARD =====
         */
        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle("-fx-background-color: white;" + "-fx-padding: 20;" + "-fx-background-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 4);");

        Label resTitle = new Label("Résultat");
        resTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        resultatLabel = new Label("Aucune analyse");
        resultatLabel.setStyle("-fx-font-size: 14px;");

        resultBox.getChildren().addAll(resTitle, resultatLabel);

        /*
         * ===== CENTER LAYOUT =====
         */
        StackPane center = new StackPane();

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(imageBox, resultBox);

        center.getChildren().add(container);

        root.setCenter(center);

        /*
         * CLICK + DRAG sur IMAGE CARD
         */
        imageBox.setOnMouseClicked(e -> openFile(stage));

        imageBox.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        imageBox.setOnDragDropped(e -> {
            var db = e.getDragboard();
            if (db.hasFiles()) {
                afficherImage(db.getFiles().get(0));
            }
            e.setDropCompleted(true);
            e.consume();
        });

        /*
         * SCENE
         */
        Scene scene = new Scene(root, 900, 650);

        stage.setScene(scene);
        stage.setTitle("Reconnaissance Faciale");

        stage.show();
    }

    /*
     * OPEN FILE
     */
    private void openFile(Stage stage) {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Choisir une image");

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images PGM", "*.pgm"));

        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            afficherImage(file);
        }
    }

    /*
     * DISPLAY IMAGE
     */
    private void afficherImage(File fichier) {

        try {

            Image image = PGMLoader.loadPGM(fichier);

            imageView.setImage(image);

            texte.setVisible(false);

            resultatLabel.setText("Analyse en cours...");
            analyserImage(fichier);

        } catch (Exception e) {

            resultatLabel.setText("Erreur chargement image");
            e.printStackTrace();
        }
    }

    private void analyserImage(File fichier) {
        systeme_reconnaissance.Image img = new systeme_reconnaissance.Image(fichier.getName());
        img.chargerImagePGM(fichier.getAbsolutePath());
        String racine = System.getProperty("user.dir");
        File dossierTrain = new File(racine + "/archive/train/");

        /* Chargement du data set d'entrainement */
        File[] dossiersTrain = dossierTrain.listFiles(File::isDirectory);
        if (dossiersTrain == null) {
            System.out.println("Erreur : Le dossier est introuvable.");
            return;
        }
        Arrays.sort(dossiersTrain, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        Personne[] tabPersonne = new Personne[dossiersTrain.length];
        Set<String> nomsConnus = new HashSet<>();
        int indice = 0;
        for (File f : dossiersTrain) {
            tabPersonne[indice] = new Personne(f.getName());
            tabPersonne[indice].chargerImagesDepuisDossier(f);
            nomsConnus.add(f.getName().toLowerCase());
            indice++;
        }

        /* Entrainement du systeme */
        SystemeReconnaissance systeme = new SystemeReconnaissance(0.95, tabPersonne);
        systeme.entrainer();
        ResultatIdentification result = systeme.identifier(img);
        if (result.estReconnu()) {
            resultatLabel.setText("Personne reconnue : " + result.getPersonne().getNom());
        } else {
            resultatLabel.setText("Personne inconnue");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}