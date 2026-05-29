import java.io.*;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

public class Image {

    private String nom;
    private int cote;
    private int largeur;
    private int hauteur;
    private String cheminFichier;
    private PixelGris[][] image;

    public Image(String nom) {
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCote() {
        return cote;
    }

    public void setCote(int cote) {
        this.cote = max(cote,0);
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public int getLargeur() {
        return max(largeur,0);
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = max(hauteur,0);
    }

    public int getImage(int x, int y) {
        return this.image[x][y].getNuanceGris();
    }

    public void setImage(PixelGris[][] image) {
        this.image = image;
    }

    private String lireLigne(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1 && c != '\n') {
            if (c != '\r') sb.append((char) c);
        }
        return sb.toString();
    }

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

    public Vecteur toVecteur(){
        Vecteur vecteur;
        int[] vecteurColonne = new int[this.hauteur*this.largeur];
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
                int valeur = vecteur.getComposantesAvecIndex(indice);
                image.image[i][x] = new PixelGris(x, i, valeur);
                indice++;
            }
        }
        return image;
    }


    public void convertirEnGris() {

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

            } else {
                throw new IOException("Format non supporté");
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

}
