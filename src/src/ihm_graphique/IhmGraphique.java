package ihm_graphique;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

/**
 * Application JavaFX principale de l'interface graphique de reconnaissance faciale.
 * <p>
 * Au démarrage, charge le jeu de données d'entraînement et entraîne le système de reconnaissance. Affiche ensuite une
 * fenêtre permettant à l'utilisateur d'importer une image PGM (via sélecteur de fichier ou glisser-déposer) et de
 * consulter le résultat d'identification.
 * </p>
 */
public class IhmGraphique extends Application {

    /**
     * Label d'instruction pour l'utilisateur, affiché avant l'importation d'une image.
     */
    private Label texte;

    /** Affiche l'image importée par l'utilisateur. */
    private ImageView imageView;

    /** Affiche la meilleure image d'entraînement correspondante après identification. */
    private ImageView imageResultat;

    /** Affiche le résultat de l'identification (nom, distance ou message d'erreur). */
    private Label resultatLabel;

    /** Système de reconnaissance faciale entraîné. */
    private SystemeReconnaissance systeme;

    /**
     * Initialise et affiche la fenêtre principale de l'application. Charge toutes les personnes d'entraînement depuis
     * {@code <répertoire-courant>/archive/train/}, entraîne le système de reconnaissance par ACP, puis construit et affiche
     * l'interface.
     *
     * @param stage le stage principal fourni par le runtime JavaFX
     */
    @Override
    public void start(Stage stage) {

        String racine = System.getProperty("user.dir");
        File dossierTrain = new File(racine + "/archive/train/");

        File[] dossiersTrain = dossierTrain.listFiles(File::isDirectory);
        if (dossiersTrain == null) {
            System.out.println("Erreur : dossier introuvable.");
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

        systeme = new SystemeReconnaissance(0.95, tabPersonne);
        systeme.entrainer();

        BorderPane root = new BorderPane();

        Label title = new Label("Reconnaissance Faciale");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnVisuels = new Button("Visuels ACP (visage moyen, eigenfaces…)");
        btnVisuels.setOnAction(e -> IhmVisuels.ouvrirFenetre());

        VBox top = new VBox(10, title, btnVisuels);
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15;");

        root.setTop(top);

        VBox imageBox = new VBox(10);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle("-fx-background-color: white;" + "-fx-padding: 20;" + "-fx-background-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 4);");
        imageBox.setMaxWidth(450);
        imageBox.setMinWidth(450);

        Label importLabel = new Label("Importer une image");
        importLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        texte = new Label("Glisser-déposer ou cliquer");
        texte.setStyle("-fx-font-size: 14px;");

        imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);

        imageBox.getChildren().addAll(importLabel, texte, imageView);

        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle("-fx-background-color: white;" + "-fx-padding: 20;" + "-fx-background-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 4);");
        resultBox.setMaxWidth(450);
        resultBox.setMinWidth(450);

        Label resTitle = new Label("Résultat");
        resTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        resultatLabel = new Label("Aucune analyse effectuée");
        resultatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        imageResultat = new ImageView();
        imageResultat.setFitWidth(150);
        imageResultat.setFitHeight(150);
        imageResultat.setPreserveRatio(true);

        resultBox.getChildren().addAll(resTitle, imageResultat, resultatLabel);

        VBox centerContainer = new VBox(20);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setStyle("-fx-background-color: #f5f6fa; -fx-padding: 25;");
        centerContainer.getChildren().addAll(imageBox, resultBox);

        root.setCenter(centerContainer);

        imageBox.setOnMouseClicked(e -> ouvrirFichier(stage));

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

        Scene scene = new Scene(root, 650, 750);
        stage.setScene(scene);
        stage.setTitle("Reconnaissance Faciale");
        stage.show();
    }

    /**
     * Ouvre un sélecteur de fichier limité aux images PGM et charge le fichier sélectionné.
     *
     * @param stage le stage propriétaire utilisé pour afficher la boîte de dialogue modale
     */
    private void ouvrirFichier(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images PGM", "*.pgm"));
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            afficherImage(file);
        }
    }

    /**
     * Charge et affiche une image PGM, puis déclenche l'identification.
     *
     * @param fichier le fichier PGM à afficher et analyser
     */
    private void afficherImage(File fichier) {
        try {
            Image image = PGMLoader.loadPGM(fichier);
            imageView.setImage(image);
            texte.setVisible(false);
            resultatLabel.setText("Analyse en cours…");
            analyserImage(fichier);
        } catch (Exception e) {
            resultatLabel.setText("Erreur lors du chargement de l'image");
            e.printStackTrace();
        }
    }

    /**
     * Lance la reconnaissance sur le fichier donné et met à jour la carte résultat. Si la personne est reconnue,
     * {@link #resultatLabel} affiche son nom et la distance de projection, et {@link #imageResultat} affiche la première
     * image d'entraînement de son dossier. Dans le cas contraire, un message "Personne inconnue" est affiché.
     *
     * @param fichier le fichier PGM à identifier
     */
    private void analyserImage(File fichier) {
        systeme_reconnaissance.Image img = new systeme_reconnaissance.Image(fichier.getName());
        img.chargerImagePGM(fichier.getAbsolutePath());
        ResultatIdentification result = systeme.identifier(img);

        if (result.estReconnu()) {
            resultatLabel.setText("Reconnu : " + result.getPersonne().getNom() + "\nDistance : " + String.format("%.1f", result.getDistance()));

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

    /**
     * Point d'entrée de l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}