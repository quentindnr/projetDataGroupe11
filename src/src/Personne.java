public class Personne {

    private String nom;
    private Image image;

    public Personne(String nom){
        this.nom = nom;
    }

    public ajouterImage(Image image){
        this.image = image;
    }

    public String getNom() {
        return nom;
    }

    public getImage(){
        return image;
    }

}
