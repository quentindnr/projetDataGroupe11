import java.io.File;

public class Personne {

    private String nom;
    private Image[] image;

    public Personne(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public Image getImageIndice(int indice) {
        return image[indice];
    }

    public void getAllImagePersonne() {
        String path = "archive/" + nom;
        File dossier = new File(path);
        File[] fichiers = dossier.listFiles();

        if (fichiers == null) {
            System.out.println("Dossier introuvable : " + path);
            return;
        }

        this.image = new Image[fichiers.length];
        for (int i = 0; i < fichiers.length; i++) {
            String pathImage = fichiers[i].getPath();
            Image img = new Image(nom + "_" + i);
            img.chargerImagePGM(pathImage);
            this.image[i] = img;
        }
    }
}