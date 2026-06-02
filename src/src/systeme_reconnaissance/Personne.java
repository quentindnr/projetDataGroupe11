package systeme_reconnaissance;

import java.io.File;
import java.util.Arrays;

/**
 * Represente une personne identifiee par son nom et associee a un ensemble d'images.
 *
 * Les images d'une personne sont stockees dans un sous-dossier portant son nom, situe dans le dossier "archive".
 */
public class Personne {

    /** Nom de la personne (correspond au nom de son dossier dans "archive"). */
    private String nom;

    /** Tableau des images associees a la personne. */
    private Image[] image;

    /**
     * Construit une personne a partir de son nom.
     *
     * @param nom le nom de la personne
     */
    public Personne(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le nom de la personne.
     *
     * @return le nom de la personne
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne une image de la personne a partir de sa position dans le tableau.
     *
     * @param indice la position de l'image dans le tableau
     * @return l'image situee a la position indiquee
     */
    public Image getImageIndice(int indice) {
        return image[indice];
    }

    /**
     * Retourne l'ensemble des images de la personne.
     *
     * @return le tableau des images de la personne
     */
    public Image[] getImages() {
        if (image == null) {
            return new Image[0];
        }
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Retourne le nombre d'image de la personne.
     * 
     * @return le nombre d'image
     */

    public int getNombreImages() {
        return image == null ? 0 : image.length;
    }

    /**
     * Charge toutes les images de la personne depuis son dossier.
     *
     * La methode lit le dossier "archive/nom", puis cree et charge une image pour chaque fichier present dans ce dossier.
     * Si le dossier n'existe pas, un message d'erreur est affiche et aucune image n'est chargee.
     */
    public void getAllImagePersonne() {
        chargerImagesDepuisDossier(new File("archive/train/" + nom));
    }

    public void chargerImagesDepuisDossier(File dossier) {
        File[] fichiers = dossier.listFiles();

        if (fichiers == null) {
            System.out.println("Dossier introuvable : " + dossier.getPath());
            return;
        }

        Arrays.sort(fichiers, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        File[] imagesPgm = Arrays.stream(fichiers).filter(File::isFile).filter(fichier -> fichier.getName().toLowerCase().endsWith(".pgm"))
                .toArray(File[]::new);

        this.image = new Image[imagesPgm.length];
        for (int i = 0; i < imagesPgm.length; i++) {
            String pathImage = imagesPgm[i].getPath();
            Image img = new Image(nom + "_" + i);
            img.chargerImagePGM(pathImage);
            this.image[i] = img;
        }
    }

    /**
     * Retourne une variable String qui represnte la personne.
     *
     * @return une chaine contenant le nom de la personne et son nombre d'images
     */
    @Override
    public String toString() {
        StringBuilder strRetour = new StringBuilder();
        strRetour.append("Nom : " + this.nom);
        strRetour.append(", possede : " + this.image.length + " images");
        return strRetour.toString();
    }
}