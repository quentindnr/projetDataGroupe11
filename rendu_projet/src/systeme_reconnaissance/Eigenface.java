package systeme_reconnaissance;

/**
 * Représente une eigenface, c'est-à-dire une composante principale utilisée dans la reconnaissance faciale.
 *
 * <p>
 * Chaque instance conserve une valeur propre, un rang et le vecteur associé à cette eigenface.
 * </p>
 */
public class Eigenface {

    private double valeurPropre;
    private int rang;
    private Vecteur vecteur;

    /**
     * Crée une nouvelle eigenface avec une valeur propre et un rang donnés.
     *
     * @param valeurPropre la valeur propre associée à cette eigenface
     * @param rang         le rang de cette eigenface dans la décomposition
     */
    public Eigenface(double valeurPropre, int rang) {
        this.valeurPropre = valeurPropre;
        this.rang = rang;
    }

    /**
     * Associe un vecteur à cette eigenface.
     *
     * @param vecteur le vecteur représentant l'eigenface
     */
    public void ajoutVecteur(Vecteur vecteur) {
        this.vecteur = vecteur;
    }

    /**
     * Retourne la valeur propre de cette eigenface.
     *
     * @return la valeur propre
     */
    public double getValeurPropre() {
        return valeurPropre;
    }

    /**
     * Retourne le rang de cette eigenface.
     *
     * @return le rang
     */
    public int getRang() {
        return rang;
    }

    /**
     * Retourne le vecteur associé à cette eigenface.
     *
     * @return le vecteur de l'eigenface, ou null si aucun vecteur n'a été ajouté
     */
    public Vecteur getVecteur() {
        return vecteur;
    }
}
