public class Eigenface{

    private float valeurPropre;
    private int rang;
    private Vecteur vecteur;

    public Eigenface(float valeurPropre, int rang){
        this.valeurPropre = valeurPropre;
        this.rang = rang;
    }

    public void ajoutVecteur(Vecteur vecteur){
        this.vecteur = vecteur;
    }

    public float getValeurPropre(){
        return valeurPropre;
    }

    public int getRang(){
        return rang;
    }

    public Vecteur getVecteur(){
        return vecteur;
    }
}
