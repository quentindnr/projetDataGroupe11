/**
 * Une classe qui represente une matrice sous forme d'un tableau de vecteurs
 */
public class Matrice {
    /**
     * Une classe qui represente une matrice sous forme d'un tableau de vecteurs
     */
    private Vecteur[] vecteurs;

    /**
     * Une classe qui represente une matrice sous forme d'un tableau de vecteurs
     * 
     * @param vecteurs Les vecteurs qui forment la matrice
     */
    public Matrice(Vecteur[] vecteurs) {
        this.vecteurs = vecteurs;
    }

    /**
     * Calcule le vecteur moyen
     */
    public Vecteur calculerVecteurMoyen() {
        // TODO
        return null;
    }

    /**
     * Calcule les valeurs propres de cette matrice
     */
    public float[] calculerValeursPropres() {
        // TODO
        return null;
    }

    /**
     * Calcule le vecteur propre d'une valeur propre donne
     * Voir {@link #calculerValeursPropres() calculerValeursPropres} pour le calcul
     * des valeurs prorpes
     * 
     * @param valeurPropre La valeur propre du vecteur propre recherche
     */
    public Vecteur calculerVecteurPropre(float valeurPropre) {
        // TODO
        return null;
    }

    /**
     * Calcule la variance expliquee a l'indice k
     * 
     * @param k L'indice k
     * @return La valeur de la variance expliquee a l'indice k
     */
    public void calculerVarianceExplique(int k) {
        // TODO
    }

    /**
     * Calcule la variance expliquee cumulee (a tout les indices k)
     * 
     * @return La valeur de la variance expliquee cumulee
     */
    public void calculerVarianceExpliqueCumule() {
        // TODO
    }
}
