package systeme_reconnaissance;

public class Eigenface{

    private double valeurPropre;
    private int rang;
    private Vecteur vecteur;

    public Eigenface(double valeurPropre, int rang){
        this.valeurPropre = valeurPropre;
        this.rang = rang;
    }

    public void ajoutVecteur(Vecteur vecteur){
        this.vecteur = vecteur;
    }

    public double getValeurPropre(){
        return valeurPropre;
    }

    public int getRang(){
        return rang;
    }

    public Vecteur getVecteur(){
        return vecteur;
    }
}
