import java.io.*;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

/**
 * Classe qui represente une image en nuances de gris
 * Elle permet de charger une image au format PGM ou PPM, de la convertir en vecteur
 * (et inversement) et de l'exporter au format PGM
 */
public class Image {

    private String nom; //nom de l'image
    private int largeur; // largeur de notre image
    private int hauteur; // hauteur de notre image
    private String cheminFichier; // chemin de l'image si elle a été chargé depuis une image déjà existante
    private PixelGris[][] image; // tableau 2D qui contient notre image sous forme de nuance de gris

    /**
     * Constructeur de la classe image
     * @param nom String qui correspond au nom de l'image
     */
    public Image(String nom) {
        this.nom = nom;
    }

    /**
     * Getter qui permet de recuperer le nom de l'image
     * @return String qui correspond au nom de l'image
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter qui permet de modifier le nom de l'image
     * @param nom String qui correspond au nouveau nom de l'image
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Getter qui permet de recuperer le chemin du fichier de l'image
     * @return String qui correspond au chemin du fichier de l'image
     */
    public String getCheminFichier() {
        return cheminFichier;
    }

    /**
     * Setter qui permet de modifier le chemin du fichier de l'image
     * @param cheminFichier String qui correspond au nouveau chemin du fichier de l'image
     */
    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    /**
     * Getter qui permet de recuperer la largeur de l'image
     * @return int qui correspond a la largeur de l'image (toujours positif ou nul)
     */
    public int getLargeur() {
        return max(largeur,0);
    }

    /**
     * Setter qui permet de modifier la largeur de l'image
     * @param largeur int qui correspond a la nouvelle largeur de l'image
     */
    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    /**
     * Getter qui permet de recuperer la hauteur de l'image
     * @return int qui correspond a la hauteur de l'image
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Setter qui permet de modifier la hauteur de l'image
     * @param hauteur int qui correspond a la nouvelle hauteur de l'image (forcee a 0 si negative)
     */
    public void setHauteur(int hauteur) {
        this.hauteur = max(hauteur,0);
    }

    /**
     * Getter qui permet de recuperer la nuance de gris d'un pixel de l'image
     * @param x int qui correspond a la coordonnee en ligne du pixel
     * @param y int qui correspond a la coordonnee en colonne du pixel
     * @return int qui correspond a la nuance de gris du pixel situe en (x,y)
     */
    public int getImage(int x, int y) {
        return this.image[x][y].getNuanceGris();
    }

    /**
     * Setter qui permet de modifier le tableau de pixels de l'image
     * @param image tableau 2D de PixelGris qui correspond a la nouvelle image
     */
    public void setImage(PixelGris[][] image) {
        this.image = image;
    }

    /**
     * Methode utilitaire qui permet de lire une ligne depuis un flux d'entree
     * Elle s'arrete au caractere de fin de ligne et ignore les retours à la ligne
     * @param in InputStream qui correspond au flux d'entree a lire
     * @return String qui correspond a la ligne lue
     * @throws IOException si une erreur survient pendant la lecture du flux
     */
    private String lireLigne(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1 && c != '\n') {
            if (c != '\r') sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * Methode qui permet d'afficher l'image sous forme de chaine de caracteres
     * Chaque pixel est represente par sa nuance de gris, separe par un espace
     * et chaque ligne de l'image se termine par un retour a la ligne
     * @return String qui correspond a la representation textuelle de l'image
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i<hauteur; i++){
            for(int j = 0; j<largeur; j++){
                stringBuilder.append(image[i][j].getNuanceGris());
                stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Methode qui permet de convertir l'image en un vecteur en utilisant la technique du serpent
     * Les lignes paires sont parcourues de gauche a droite et les lignes impaires de droite a gauche
     * @return Vecteur qui contient toutes les nuances de gris de l'image mises bout a bout
     */
    public Vecteur toVecteur(){
        Vecteur vecteur;
        double[] vecteurColonne = new double[this.hauteur*this.largeur];
        int indice = 0;

        for(int i = 0; i<this.getHauteur(); i++){
            for(int j = 0; j<this.getLargeur(); j++){
                if(i%2 == 0){
                    vecteurColonne[indice] = this.getImage(i,j);
                }else{
                    vecteurColonne[indice] = this.getImage(i,this.getLargeur()-1-j);
                }
                indice++;
            }
        }
        vecteur = new Vecteur(vecteurColonne, this.getHauteur()*this.getLargeur());
        return vecteur;
    }

    /**
     * Methode qui permet de charger une image au format PGM binaire (ie le code P5) depuis un fichier
     * Elle lit l'entete pour recuperer les dimensions puis remplit le tableau de pixels
     * @param cheminFichier String qui correspond au chemin du fichier PGM a charger
     */
    public void chargerImagePGM(String cheminFichier){
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(cheminFichier)))) {

            // Lire la signature du fichier
            String magique = lireLigne(in);
            if (!magique.equals("P5")) {
                throw new IOException("Ce format n'est pas supporté");
            }

            // Lire la ligne suivante, en sautant les commentaires
            String ligne = lireLigne(in);
            while (ligne.startsWith("#")) {
                ligne = lireLigne(in);
            }

            // Largeur et hauteur
            String[] dims = ligne.trim().split("\\s+");
            this.largeur = Integer.parseInt(dims[0]);
            this.hauteur = Integer.parseInt(dims[1]);


            // Lire les pixels
            this.image = new PixelGris[hauteur][largeur];
            for (int y = 0; y < hauteur; y++) {
                for (int x = 0; x < largeur; x++) {
                    this.image[y][x] = new PixelGris(x,y,in.readUnsignedByte());
                }
            }


        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Methode statique qui permet de reconstruire une image a partir d'un vecteur
     * Elle utilise la technique du serpent inverse de la methode toVecteur pour replacer chaque pixel
     * Les dimensions sont fixees a 92x112 car c'est le format de notre base d'image open source
     * @param vecteur Vecteur qui contient les nuances de gris dans l'ordre du zigzag
     * @param nom String qui correspond au nom de l'image a creer
     * @return Image qui correspond a l'image reconstruite a partir du vecteur
     */
    public static Image fromVecteur(Vecteur vecteur, String nom) {
        int largeurImage = 92;
        int hauteurImage = 112;

        Image image = new Image(nom);
        image.setLargeur(largeurImage);
        image.setHauteur(hauteurImage);
        image.image = new PixelGris[hauteurImage][largeurImage];   // ← initialisation

        int indice = 0;
        for (int i = 0; i < hauteurImage; i++) {
            for (int j = 0; j < largeurImage; j++) {
                int x;
                if (i % 2 == 0) {
                    x = j;
                } else {
                    x = largeurImage - 1 - j;
                }
                int valeur = (int) vecteur.getComposantesAvecIndex(indice);
                image.image[i][x] = new PixelGris(x, i, valeur);
                indice++;
            }
        }
        return image;
    }


    /**
     * Methode qui permet de charger une image au format PPM ASCII (ie le code P3) depuis un fichier
     * Chaque pixel couleur (R,G,B) est converti en nuance de gris avec la formule
     * 0.299*R + 0.587*G + 0.114*B car c'est la formule qui tiens le plus compte de la sensibilité de l'oeuil
     * @param cheminFichier String qui correspond au chemin du fichier PPM a charger
     */
    public void chargerImagePPM(String cheminFichier) {

        this.setCheminFichier(cheminFichier);

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(cheminFichier)))) {

            if (lireLigne(in).equals("P3")) {

                // Largeur et hauteur (en sautant les commentaires)
                String ligne = lireLigne(in);
                while (ligne.startsWith("#")) {
                    ligne = lireLigne(in);
                }
                String[] dims = ligne.trim().split("\\s+");
                this.largeur = Integer.parseInt(dims[0]);
                this.hauteur = Integer.parseInt(dims[1]);

                // On lit (et ignore) la valeur max
                ligne = lireLigne(in);

                // Un seul Scanner, créé après l'en-tête, sur le même flux
                Scanner scanner = new Scanner(in);

                this.image = new PixelGris[hauteur][largeur];
                for (int y = 0; y < hauteur; y++) {
                    for (int x = 0; x < largeur; x++) {
                        int R = scanner.nextInt();
                        int G = scanner.nextInt();
                        int B = scanner.nextInt();
                        int gris = (int) Math.round(0.299 * R + 0.587 * G + 0.114 * B);
                        this.image[y][x] = new PixelGris(x, y, gris);
                    }
                }

                scanner.close();
            } else {
                throw new IOException("Format non supporté");
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Methode qui permet de creer un fichier PGM ASCII (ie le code P2) a partir de l'image
     * Elle ecrit l'entete PGM puis la nuance de gris de chaque pixel ligne par ligne
     * @param cheminSortie String qui correspond au chemin du fichier PGM a creer
     */
    public void creerFichierPGM(String cheminSortie) {

        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(cheminSortie)))) {

            // En-tête PGM ASCII
            out.println("P2");
            out.println(largeur + " " + hauteur);
            out.println(255);

            for (int y = 0; y < hauteur; y++) {
                for (int x = 0; x < largeur; x++) {
                    int g = image[y][x].getNuanceGris();
                    out.println(g);
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

}
