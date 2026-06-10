package ihm_graphique;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import systeme_reconnaissance.Eigenface;
import systeme_reconnaissance.Image;
import systeme_reconnaissance.Personne;
import systeme_reconnaissance.SousEspace;
import systeme_reconnaissance.Vecteur;

/**
 * Affiche dans une fenetre JavaFX les visuels demandes par le descriptif du projet : le visage moyen et des visages
 * centres, les premiers eigenfaces avec leurs valeurs propres, et la reconstruction d'un visage pour plusieurs valeurs
 * de K.
 *
 * Les eigenfaces et les visages centres sont normalises min->0 / max->255 pour rester visibles ; le visage moyen et les
 * reconstructions sont simplement bornes a [0,255].
 */
public class IhmVisuels extends Application {

    private static final int NB_EIGENFACES = 10;
    private static final int NB_VISAGES_EXEMPLE = 3;
    private static final int TAILLE_AFFICHAGE = 130;

    @Override
    public void start(Stage stage) {
        ScrollPane contenu = construireContenu();
        stage.setScene(new Scene(contenu, 1000, 800));
        stage.setTitle("PCA Visuals - Facial Recognition");
        stage.show();

        // Rendu hors ecran pour verification (-Dvisuels.snapshot=chemin.png)
        String cheminSnapshot = System.getProperty("visuels.snapshot");
        if (cheminSnapshot != null) {
            Platform.runLater(() -> {
                sauverPng(contenu.getContent().snapshot(new SnapshotParameters(), null), new File(cheminSnapshot));
                Platform.exit();
            });
        }
    }

    /**
     * Ouvre les visuels dans une nouvelle fenetre. A appeler depuis le thread JavaFX (par exemple depuis un bouton de l'IHM
     * principale), une fois l'application JavaFX deja demarree.
     */
    public static void ouvrirFenetre() {
        IhmVisuels visuels = new IhmVisuels();
        Stage stage = new Stage();
        stage.setScene(new Scene(visuels.construireContenu(), 1000, 800));
        stage.setTitle("PCA Visuals - Facial Recognition");
        stage.show();
    }

    /**
     * Construit la vue des visuels : visage moyen, eigenfaces et valeurs propres, visages centres et reconstruction pour
     * plusieurs K.
     *
     * @return le conteneur defilant regroupant toutes les sections de visuels
     */
    private ScrollPane construireContenu() {
        SousEspace sousEspace = construireSousEspace();
        if (sousEspace == null) {
            Label erreur = new Label("Training folder not found (archive/train).");
            erreur.setStyle("-fx-font-size: 14px; -fx-text-fill: #c0392b; -fx-padding: 20;");
            return new ScrollPane(new VBox(erreur));
        }
        Eigenface[] eigenfaces = sousEspace.getEigenfaces();
        Vecteur moyen = sousEspace.getVecteurMoyen();

        VBox racine = new VBox(25);
        racine.setPadding(new Insets(20));
        racine.setPrefWidth(960);
        racine.setStyle("-fx-background-color: #ecf0f1;");

        // Visage moyen
        racine.getChildren().add(section("Mean face", vignette(toImage(borner(moyen)), "mean")));

        // Premiers eigenfaces et valeurs propres
        FlowPane grilleEigenfaces = new FlowPane(15, 15);
        int nbEigenfaces = Math.min(NB_EIGENFACES, eigenfaces.length);
        for (int i = 0; i < nbEigenfaces; i++) {
            grilleEigenfaces.getChildren()
                    .add(vignette(toImage(normaliser(eigenfaces[i].getVecteur())), String.format("#%d  λ=%.0f", i, eigenfaces[i].getValeurPropre())));
        }
        racine.getChildren().add(section("First eigenfaces and eigenvalues", grilleEigenfaces));

        // Visages d'exemple et leurs versions centrees
        racine.getChildren().add(section("Faces and centered versions", grilleCentrage(moyen)));

        // Reconstruction pour plusieurs K
        racine.getChildren().add(section("Reconstruction for several K", grilleReconstruction(sousEspace, eigenfaces, moyen)));

        ScrollPane defilement = new ScrollPane(racine);
        defilement.setFitToWidth(true);
        return defilement;
    }

    /** Visages d'exemple charges en meme temps que la base. */
    private final ArrayList<Vecteur> exemples = new ArrayList<>();

    /**
     * Charge la base d'apprentissage (archive/train), construit le sous-espace des eigenfaces et memorise quelques visages
     * d'exemple.
     *
     * @return le sous-espace des eigenfaces, ou null si le dossier d'apprentissage est introuvable
     */
    private SousEspace construireSousEspace() {
        File dossierTrain = new File(System.getProperty("user.dir") + "/archive/train/");
        File[] dossiers = dossierTrain.listFiles(File::isDirectory);
        if (dossiers == null) {
            return null;
        }
        Arrays.sort(dossiers, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        ArrayList<Vecteur> vecteurs = new ArrayList<>();
        for (File dossier : dossiers) {
            Personne personne = new Personne(dossier.getName());
            personne.chargerImagesDepuisDossier(dossier);
            for (Image image : personne.getImages()) {
                vecteurs.add(image.toVecteur());
            }
            if (exemples.size() < NB_VISAGES_EXEMPLE && personne.getNombreImages() > 0) {
                exemples.add(personne.getImageIndice(0).toVecteur());
            }
        }

        SousEspace sousEspace = new SousEspace(vecteurs.toArray(new Vecteur[0]));
        sousEspace.calculerEigenface(0.99);
        return sousEspace;
    }

    /**
     * Construit la grille des visages d'exemple avec leur version centree (visage - visage moyen).
     *
     * @param moyen le visage moyen a soustraire pour centrer les visages
     * @return la grille des visages originaux et de leurs versions centrees
     */
    private FlowPane grilleCentrage(Vecteur moyen) {
        FlowPane grille = new FlowPane(15, 15);
        for (int i = 0; i < exemples.size(); i++) {
            grille.getChildren().add(vignette(toImage(borner(exemples.get(i))), "face " + i));
            Vecteur centre = exemples.get(i).copier();
            centre.soustraire(moyen);
            grille.getChildren().add(vignette(toImage(normaliser(centre)), "centered " + i));
        }
        return grille;
    }

    /**
     * Construit la grille des reconstructions d'un visage pour plusieurs valeurs de K.
     *
     * @param sousEspace le sous-espace des eigenfaces
     * @param eigenfaces les eigenfaces (axes principaux)
     * @param moyen      le visage moyen
     * @return la grille des reconstructions pour les differentes valeurs de K
     */
    private FlowPane grilleReconstruction(SousEspace sousEspace, Eigenface[] eigenfaces, Vecteur moyen) {
        FlowPane grille = new FlowPane(15, 15);
        Vecteur cible = exemples.get(0);
        Vecteur projection = sousEspace.projeter(cible);
        int[] valeursK = { 1, 5, 10, 20, eigenfaces.length };
        for (int k : valeursK) {
            int kEffectif = Math.min(k, eigenfaces.length);
            Vecteur reconstruction = reconstruire(moyen, eigenfaces, projection, kEffectif);
            double erreur = cible.distance(reconstruction);
            grille.getChildren().add(vignette(toImage(borner(reconstruction)), String.format("K=%d  err=%.0f", kEffectif, erreur)));
        }
        return grille;
    }

    /**
     * Reconstruit un visage a partir du visage moyen et des k premieres composantes : visage moyen plus
     * la somme des scores ponderant les eigenfaces.
     *
     * @param moyen      le visage moyen du sous-espace
     * @param eigenfaces les eigenfaces (axes principaux)
     * @param projection les coordonnees projetees du visage
     * @param k          le nombre de composantes a utiliser
     * @return le vecteur image reconstruit
     */
    private static Vecteur reconstruire(Vecteur moyen, Eigenface[] eigenfaces, Vecteur projection, int k) {
        Vecteur reconstruction = moyen.copier();
        int dimension = reconstruction.getDimension();
        for (int i = 0; i < k; i++) {
            double coordonnee = projection.getComposantesAvecIndex(i);
            Vecteur eigenface = eigenfaces[i].getVecteur();
            for (int p = 0; p < dimension; p++) {
                reconstruction.setComposantesAvecIndex(p, reconstruction.getComposantesAvecIndex(p) + coordonnee * eigenface.getComposantesAvecIndex(p));
            }
        }
        return reconstruction;
    }

    // ----------------------------------------------------------------------------------------------
    // Affichage

    /**
     * Cree une section titree contenant un noeud (image ou grille).
     *
     * @param titre   le titre de la section
     * @param contenu le noeud affiche sous le titre
     * @return la boite regroupant le titre et le contenu
     */
    private static VBox section(String titre, javafx.scene.Node contenu) {
        Label label = new Label(titre);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        VBox boite = new VBox(10, label, contenu);
        boite.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        return boite;
    }

    /**
     * Cree une vignette : une image agrandie surmontee de sa legende.
     *
     * @param image   l'image a afficher
     * @param legende le texte affiche sous l'image
     * @return la vignette (image + legende)
     */
    private static VBox vignette(javafx.scene.image.Image image, String legende) {
        ImageView vue = new ImageView(image);
        vue.setFitWidth(TAILLE_AFFICHAGE);
        vue.setPreserveRatio(true);
        vue.setSmooth(false);
        Label label = new Label(legende);
        label.setStyle("-fx-font-size: 12px;");
        VBox boite = new VBox(5, vue, label);
        boite.setAlignment(Pos.CENTER);
        return boite;
    }

    /**
     * Convertit un vecteur deja en niveaux de gris [0,255] (ordre serpent) en image JavaFX.
     *
     * @param niveauxGris le vecteur image en niveaux de gris
     * @return l'image JavaFX correspondante
     */
    private static javafx.scene.image.Image toImage(Vecteur niveauxGris) {
        Image image = Image.fromVecteur(niveauxGris, "visuel");
        int largeur = image.getLargeur();
        int hauteur = image.getHauteur();
        WritableImage rendu = new WritableImage(largeur, hauteur);
        PixelWriter pixels = rendu.getPixelWriter();
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                double gris = image.getImage(y, x) / 255.0;
                gris = Math.max(0, Math.min(1, gris));
                pixels.setColor(x, y, Color.gray(gris));
            }
        }
        return rendu;
    }

    /**
     * Borne chaque composante dans [0,255].
     *
     * @param vecteur le vecteur a borner
     * @return un nouveau vecteur dont les composantes sont dans [0,255]
     */
    private static Vecteur borner(Vecteur vecteur) {
        int dimension = vecteur.getDimension();
        double[] valeurs = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            valeurs[i] = Math.max(0, Math.min(255, vecteur.getComposantesAvecIndex(i)));
        }
        return new Vecteur(valeurs, dimension);
    }

    /**
     * Normalise lineairement les composantes sur [0,255] (min -> 0, max -> 255).
     *
     * @param vecteur le vecteur a normaliser
     * @return un nouveau vecteur normalise sur [0,255]
     */
    private static Vecteur normaliser(Vecteur vecteur) {
        int dimension = vecteur.getDimension();
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < dimension; i++) {
            double valeur = vecteur.getComposantesAvecIndex(i);
            min = Math.min(min, valeur);
            max = Math.max(max, valeur);
        }
        double amplitude = max - min;
        double[] valeurs = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            valeurs[i] = amplitude == 0 ? 0 : 255.0 * (vecteur.getComposantesAvecIndex(i) - min) / amplitude;
        }
        return new Vecteur(valeurs, dimension);
    }

    /**
     * Enregistre une image JavaFX au format PNG (utilise pour la verification hors ecran).
     *
     * @param image   l'image JavaFX a enregistrer
     * @param fichier le fichier PNG de destination
     */
    private static void sauverPng(javafx.scene.image.Image image, File fichier) {
        int largeur = (int) image.getWidth();
        int hauteur = (int) image.getHeight();
        java.awt.image.BufferedImage tampon = new java.awt.image.BufferedImage(largeur, hauteur, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        javafx.scene.image.PixelReader lecteur = image.getPixelReader();
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                tampon.setRGB(x, y, lecteur.getArgb(x, y));
            }
        }
        try {
            javax.imageio.ImageIO.write(tampon, "png", fichier);
        } catch (java.io.IOException e) {
            System.out.println("Erreur ecriture PNG : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
