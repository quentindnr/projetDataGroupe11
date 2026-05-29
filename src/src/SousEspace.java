/**
 * Une classe qui represente le sous-ensemble de projection
 */
public class SousEspace {
    /**
     * La matrice du sous-ensemble
     */
    private Matrice matrice;
    /**
     * Les eigenfaces du sous-ensemble
     */
    private Eigenface[] eigenfaces;

    /**
     * Une classe qui represente le sous-ensemble de projection
     * 
     * @param vecteurs Les vecteurs qui forment le sous-ensemble
     */
    public SousEspace(Vecteur[] vecteurs) {
        this.matrice = new Matrice(vecteurs);
        this.eigenfaces = null;
    }

    /**
     * Calcule les eigenfaces
     */
    public void calculerEigenface() {
        // TODO
        // calc vecteur moyen
        // centrer la matrice
        // calculer les valeurs propres
        // prendre les n plus grandes (methode du coude)
        // pour chacune, calculer les vecteurs propres
        // construire la liste les eigenfaces et mettre dans eigenfaces
    }

    /**
     * Projete l'image sur ce sous-ensemble
     * 
     * @param image L'image a projeter
     */
    public void projeter(Image image) {
        // TODO
    }

    /**
     * Reconstruit une image a partir de la projection
     */
    public void reconstruire(Image projection) {
        // TODO
    }
}
