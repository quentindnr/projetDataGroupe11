public class Personne {

    private String nom;
    private Image image;

    public Personne(String nom){
        this.nom = nom;
    }

    public void ajouterImage(Image image){
        this.image = image;
    }

    public String getNom() {
        return nom;
    }

    public Image getImage(){
        return image;
    }

}
