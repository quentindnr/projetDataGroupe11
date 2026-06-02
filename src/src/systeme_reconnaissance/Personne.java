package systeme_reconnaissance;

import java.io.File;

/**
 * Represente une personne identifiee par son nom et associee a un ensemble
 * d'images.
 *
 * Les images d'une personne sont stockees dans un sous-dossier portant son nom,
 * situe dans le dossier "archive".
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
    public Image[] getImage() {
        return this.image;
    }

    /**
     * Charge toutes les images de la personne depuis son dossier.
     *
     * La methode lit le dossier "archive/nom", puis cree et charge une image
     * pour chaque fichier present dans ce dossier. Si le dossier n'existe pas,
     * un message d'erreur est affiche et aucune image n'est chargee.
     */
    public void getAllImagePersonne() {
        String path = "archive/" + nom;
        File dossier = new File(path);
        File[] fichiers = dossier.listFiles();

        // Si le dossier n'existe pas, listFiles() renvoie null : on arrete le chargement
        if (fichiers == null) {
            System.out.println("Dossier introuvable : " + path);
            return;
        }

        // On cree un tableau de la taille du nombre de fichiers trouves
        this.image = new Image[fichiers.length];

        // Pour chaque fichier, on cree une image et on charge son contenu
        for (int i = 0; i < fichiers.length; i++) {
            String pathImage = fichiers[i].getPath();
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