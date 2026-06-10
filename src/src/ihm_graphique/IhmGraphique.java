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
    private ImageView imageResultat;
    private Label resultatLabel;
    private systeme_reconnaissance.SystemeReconnaissance systeme;

    @Override
    public void start(Stage stage) {

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
        systeme = new SystemeReconnaissance(0.95, tabPersonne);
        systeme.entrainer();

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

        // Limite la largeur pour éviter l'effet "bandeau étiré"
        imageBox.setMaxWidth(450);
        imageBox.setMinWidth(450);

        Label importLabel = new Label("Import Image");
        importLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        texte = new Label("Drag & Drop ou clic");
        texte.setStyle("-fx-font-size: 14px;");

        imageView = new ImageView();
        imageView.setFitWidth(250); // Légèrement réduit pour laisser de la place verticalement
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);

        imageBox.getChildren().addAll(importLabel, texte, imageView);

        /*
         * ===== RESULT CARD =====
         */
        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle("-fx-background-color: white;" + "-fx-padding: 20;" + "-fx-background-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 4);");

        // Même largeur que la carte du dessus pour une belle harmonie visuelle
        resultBox.setMaxWidth(450);
        resultBox.setMinWidth(450);

        Label resTitle = new Label("Résultat");
        resTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        resultatLabel = new Label("Aucune analyse");
        resultatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        imageResultat = new ImageView();
        imageResultat.setFitWidth(150);
        imageResultat.setFitHeight(150);
        imageResultat.setPreserveRatio(true);

        resultBox.getChildren().addAll(resTitle, imageResultat, resultatLabel);

        /*
         * ===== CENTER LAYOUT (CORRIGÉ POUR VERSION EMPILÉE) ===== Une VBox globale pour empiler les cartes au milieu de
         * l'écran
         */
        VBox centerContainer = new VBox(20); // 20px d'espace vertical entre les deux blocs
        centerContainer.setAlignment(Pos.CENTER);
        // Fond légèrement gris pour faire ressortir l'ombre des cartes blanches
        centerContainer.setStyle("-fx-background-color: #f5f6fa; -fx-padding: 25;");

        centerContainer.getChildren().addAll(imageBox, resultBox);

        root.setCenter(centerContainer);

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
        // Taille de fenêtre adaptée pour le mode vertical (plus haute que large)
        Scene scene = new Scene(root, 650, 750);

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
        ResultatIdentification result = systeme.identifier(img);

        if (result.estReconnu()) {
            resultatLabel.setText("Personne reconnue : " + result.getPersonne().getNom() + "\nDistance : " + String.format("%.1f", result.getDistance()));

            String racine = System.getProperty("user.dir");
            File dossierPersonne = new File(racine + "/archive/train/" + result.getPersonne().getNom() + "/");
            File[] images = dossierPersonne.listFiles((d, n) -> n.endsWith(".pgm"));

            if (images != null && images.length > 0) {
                Arrays.sort(images);
                try {
                    imageResultat.setImage(PGMLoader.loadPGM(images[0]));
                } catch (Exception e) {
                    imageResultat.setImage(null);
                    e.printStackTrace();
                }
            }
        } else {
            resultatLabel.setText("Personne inconnue");
            imageResultat.setImage(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}