import java.io.*;
import java.util.Scanner;

public class Image {

    private String nom;
    private int cote;
    private int largeur;
    private int hauteur;
    private String cheminFichier;
    private PixelGris[][] image;

    public Image(String nom, String cheminFichier) {
        this.nom = nom;
        this.cheminFichier = cheminFichier;

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


            // Lire les pixels (données binaires : 1 octet par pixel)
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
        this.cote = cote;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
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


}
