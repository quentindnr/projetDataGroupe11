import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;

/**
 * Une classe qui represente la matrice de vecteurs sous forme d'une
 * SimpleMatrix
 */
public class Matrice {
    /**
     * La matrice de vecteurs
     */
    private SimpleMatrix matrice;

    /**
     * Une classe qui represente la matrice de vecteurs sous forme d'une
     * SimpleMatrix
     * 
     * @param vecteurs Les vecteurs qui forment la matrice
     */
    public Matrice(Vecteur[] vecteurs) {
        this.matrice = new SimpleMatrix(vecteurs.length, vecteurs[0].getDimensions());
        for (int i = 0; i < vecteurs.length; i++) {
            Vecteur vecteur = vecteurs[i];
            for (int j = 0; j < vecteur.getDimensions(); j++) {
                matrice.set(i, j, vecteur.getComposantesAvecIndex(j));
            }
        }
    }

    /**
     * Calcule le vecteur moyen
     */
    public Vecteur calculerVecteurMoyen(SimpleMatrix matrice) {
        int nbVecteurs = matrice.getNumRows();
        int dimension  = matrice.getNumCols();

        double[] composantes = new double[dimension];

        for (int j = 0; j < dimension; j++) {
            double somme = 0;
            for (int i = 0; i < nbVecteurs; i++) {
                somme += matrice.get(i, j);
            }
            composantes[j] = somme / nbVecteurs;
        }

        return new Vecteur(composantes,composantes.length);
    }

    /**
     * Calcule les valeurs propres de cette matrice
     */
    public float[] calculerValeursPropres(SimpleMatrix matrice) {

        var eig = this.matrice.eig();
        int numEigenvalues = eig.getNumberOfEigenvalues();
        float[] valeursPropres = new float[numEigenvalues];

        for(int i = 0; i < numEigenvalues; i++){
            valeursPropres[i] = (float)eig.getEigenvalue(i).real;
        }

        return valeursPropres;
    }

    /**
     * Calcule le vecteur propre d'une valeur propre donne
     * Voir {@link #calculerValeursPropres() calculerValeursPropres} pour le calcul
     * des valeurs prorpes
     * 
     * @param valeurPropre La valeur propre du vecteur propre recherche
     */
    public Vecteur calculerVecteurPropre(float valeurPropre) {
        
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
