/**
 * Une classe qui represente un sous-ensemble
 */
public class SousEspace {
    /**
     * Le vecteur correspondant au visage moyen
     */
    private Vecteur visageMoyen;
    /**
     * La dimension du sous-espace
     */
    private int dimension;
    /**
     * Le nombre d'eigen faces
     */
    private int nbEigenFaces;

    /**
     * Cree une instance de SousEspace
     * 
     * @param visageMoyen  Le vecteur correspondant au visage moyen
     * @param dimension    La dimension du sous-espace
     * @param nbEigenFaces Le nombre d'eigen faces
     */
    public SousEspace(Vecteur visageMoyen, int dimension, int nbEigenFaces) {
        this.visageMoyen = visageMoyen;
        this.dimension = dimension;
        this.nbEigenFaces = nbEigenFaces;
    }

    /**
     * Calcule les eigenfaces
     */
    public void calculerEigenface() {
        // TODO
    }

    /**
     * Projete l'image sur les eigenfaces
     * 
     * @param image L'image a projeter
     */
    public Image projeter(Image image) {
        // TODO
    }

    /**
     * Reconstruit une image a partir de la projection
     */
    public Image reconstruire(Image projection) {
        // TODO
    }

    /**
     * Calcule le visage moyen de ce sous-ensemble
     * 
     * @param images La base d'images
     */
    public void calculerVisageMoyen(Image[] images) {
        // TODO
    }

    /**
     * Calcule la variance expliquee a l'indice k
     * 
     * @param k L'indice k
     * @return La valeur de la variance expliquee a l'indice k
     */
    public float calculerVarianceExplique(int k) {
        // TODO
    }

    /**
     * Calcule la variance expliquee cumulee (a tout les indices k)
     * 
     * @return La valeur de la variance expliquee cumulee
     */
    public float calculerVarianceExpliqueCumule() {
        // TODO
    }
}
