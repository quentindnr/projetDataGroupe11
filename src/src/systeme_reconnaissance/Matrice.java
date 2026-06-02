package systeme_reconnaissance;

import org.ejml.simple.SimpleMatrix;

/**
 * Une classe qui represente la matrice de vecteurs sous forme d'une SimpleMatrix
 */
public class Matrice extends SimpleMatrix {

    /**
     * Une classe qui represente la matrice de vecteurs sous forme d'une SimpleMatrix
     * 
     * @param vecteurs Les vecteurs qui forment la matrice
     */
    public Matrice(Vecteur[] vecteurs) {
        super(vecteurs[0].getDimension(), vecteurs.length);
        for (int i = 0; i < vecteurs.length; i++) {
            Vecteur vecteur = vecteurs[i];
            for (int j = 0; j < vecteur.getDimension(); j++) {
                set(j, i, vecteur.getComposantesAvecIndex(j));
            }
        }
    }

    /**
     * Cree une matrice vide
     * 
     * @param numRows Le nombre de lignes de la matrice
     * @param numCols Le nombre de colonnes de la matrice
     */
    public Matrice(int numRows, int numCols) {
        super(numRows, numCols);
    }

    /**
     * Cree une copie d'une SimpleMatrix sous la forme d'une Matrice
     * 
     * @param matrice La matrice a copier
     */
    public Matrice(SimpleMatrix matrice) {
        super(matrice);
    }

    /**
     * Calcule le vecteur moyen de tous les vecteurs de la matrice
     *
     * @return Le vecteur moyen de dimension egale a la dimension des vecteurs
     */
    public Vecteur getVecteurMoyen() {
        int nbVecteurs = getNumCols();
        int dimension = getNumRows();

        double[] composantes = new double[dimension];

        for (int j = 0; j < dimension; j++) {
            double somme = 0;
            for (int i = 0; i < nbVecteurs; i++) {
                somme += get(j, i);
            }
            composantes[j] = somme / nbVecteurs;
        }

        return new Vecteur(composantes, composantes.length);
    }

    /**
     * Calcule la matrice centree
     * 
     * @return La matrice centree
     */
    public Matrice getMatriceCentree() {
        Matrice matriceCentree = new Matrice(getNumRows(), getNumCols());
        Vecteur vecteurMoyen = getVecteurMoyen();

        SimpleMatrix ligneMoyenne = new SimpleMatrix(getNumRows(), 1);
        for (int j = 0; j < getNumRows(); j++) {
            ligneMoyenne.set(j, 0, vecteurMoyen.getComposantesAvecIndex(j));
        }

        for (int i = 0; i < getNumCols(); i++) {
            SimpleMatrix ligneCentree = cols(i, i + 1).minus(ligneMoyenne);
            matriceCentree.insertIntoThis(0, i, ligneCentree);
        }

        return matriceCentree;
    }

    /**
     * Calcule la matrice de covariance (a une constance pres)
     * 
     * @return La matrice de covariance
     */
    public Matrice getMatriceCovariance() {
        SimpleMatrix cov = transpose().mult(this);
        cov = cov.scale(1.0 / getNumCols());
        return new Matrice(cov);
    }

    /**
     * Calcule les valeurs propres de la matrice
     *
     * @return La decomposition EVD de cette matrice
     */
    public EVDCache calculerValeursPropres() {
        return new EVDCache(eig());
    }

}
