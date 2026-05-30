import java.util.Arrays;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleEVD;
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
     * Cache de la décomposition en valeurs/vecteurs propres
     */
    private SimpleEVD<SimpleMatrix> evdCache;

    /**
     * Nombre de valeurs propres de la matrice de covariance
     */
    private int numEigenvalues;
    
    /**
     * Somme de toutes les valeurs propres, utilisee pour le calcul des variances
     */
    private double somme; 

    /**
     * Valeurs propres triees par ordre decroissant
     * Initialisees lors du premier appel a {@link #calculerValeursPropres()}
     */
    private double[] valeursPropresTriees;

    /**
     * Une classe qui represente la matrice de vecteurs sous forme d'une
     * SimpleMatrix
     * 
     * @param vecteurs Les vecteurs qui forment la matrice
     */
    public Matrice(Vecteur[] vecteurs) {
        this.matrice = new SimpleMatrix(vecteurs[0].getDimension(), vecteurs.length);
        for (int i = 0; i < vecteurs.length; i++) {
            Vecteur vecteur = vecteurs[i];
            for (int j = 0; j < vecteur.getDimension(); j++) {
                matrice.set(j, i, vecteur.getComposantesAvecIndex(j));
            }
        }
    }
    /**
     * Calcule le vecteur moyen de tous les vecteurs de la matrice
     *
     * @return Le vecteur moyen de dimension egale a la dimension des vecteurs
     */
    public Vecteur calculerVecteurMoyen() {
        int nbVecteurs = this.matrice.getNumCols();
        int dimension  = this.matrice.getNumRows();

        double[] composantes = new double[dimension];

        for (int j = 0; j < dimension; j++) {
            double somme = 0;
            for (int i = 0; i < nbVecteurs; i++) {
                somme += matrice.get(j, i);
            }
            composantes[j] = somme / nbVecteurs;
        }

        return new Vecteur(composantes,composantes.length);
    }

   /**
     * Calcule les valeurs propres de la matrice de covariance
     * Centre les donnees par rapport au vecteur moyen avant le calcul
     * Met en cache la decomposition EVD, la somme et les valeurs triees
     *
     * @return Un tableau de float contenant les valeurs propres
     */
    public float[] calculerValeursPropres() {
        
        Vecteur vecteurMoyen = this.calculerVecteurMoyen();
        SimpleMatrix matricePropre = new SimpleMatrix(this.matrice.getNumRows(), this.matrice.getNumCols());

        double[] moyenneComposantes = vecteurMoyen.getComposantes(); 
        SimpleMatrix ligneMoyenne = new SimpleMatrix(this.matrice.getNumRows(), 1);
        for (int j = 0; j < this.matrice.getNumRows(); j++) {
            ligneMoyenne.set(j, 0, moyenneComposantes[j]);
        }


        // Itérer sur chaque ligne (chaque vecteur)
        for (int i = 0; i < this.matrice.getNumCols(); i++) {
            SimpleMatrix ligneCentree = this.matrice.cols(i, i + 1).minus(ligneMoyenne);
            matricePropre.insertIntoThis(0, i, ligneCentree);
        }

        this.matrice = matricePropre;
        SimpleMatrix cov = this.matrice.transpose().mult(this.matrice);
        cov = cov.scale(1.0 / this.matrice.getNumCols());
        
        this.evdCache = cov.eig();
        this.numEigenvalues = this.evdCache.getNumberOfEigenvalues();
        float[] valeursPropres = new float[this.numEigenvalues];

        for(int i = 0; i < this.numEigenvalues; i++){
            valeursPropres[i] = (float)this.evdCache.getEigenvalue(i).real;
        }
        calculerSomme();
        trierValeurPropre();
        return valeursPropres;
    }

    /**
     * Calcule le vecteur propre d'une valeur propre donne
     * Voir {@link #calculerValeursPropres() calculerValeursPropres} pour le calcul
     * des valeurs prorpes
     * 
     * @param valeurPropre La valeur propre du vecteur propre recherche
     */
    public SimpleMatrix calculerVecteurPropre(float valeurPropre){
        if (this.evdCache == null) {
            throw new IllegalStateException("Les valeurs propres doivent être calculées avant de calculer les vecteurs propres.");
        }

        for (int i = 0; i < this.numEigenvalues; i++) {
            if (Math.abs(this.evdCache.getEigenvalue(i).real - valeurPropre) < 1e-6) {
                return this.evdCache.getEigenVector(i);
            }
        }

        throw new IllegalArgumentException("Aucun vecteur propre trouvé pour la valeur propre donnée: " + valeurPropre);
    }   

    /**
     * Calcule la variance expliquee a l'indice k
     * Les valeurs propres sont triees par ordre decroissant
     * Voir {@link #calculerValeursPropres()} avant d'appeler cette methode
     *
     * @param k L'indice k de la valeur propre
     * @return La variance expliquee a l'indice k entre 0 et 1
     */
    public double calculerVarianceExplique(int k) {
        return this.valeursPropresTriees[k]/ this.somme;
    }

    /**
     * Calcule la variance expliquee cumulee jusqu'a l'indice k
     * Les valeurs propres sont triees par ordre decroissant
     * Voir {@link #calculerValeursPropres()} avant d'appeler cette methode
     *
     * @param k L'indice k jusqu'auquel sommer les variances
     * @return La variance expliquee cumulee entre 0 et 1
     */
    public double calculerVarianceExpliqueCumule(int k) {
        double sommeV = 0; 

        for (int i = 0; i < k; i++){
            sommeV += this.valeursPropresTriees[i];
        }



        return sommeV/this.somme;
    }


    /**
     * Retourne une representation textuelle de la matrice
     *
     * @return La matrice sous forme de chaine de caracteres
     */
    @Override
    public String toString() {
        return this.matrice.toString();
    }

    /**
     * Calcule et met en cache la somme de toutes les valeurs propres
     * Doit être appelée après {@link #calculerValeursPropres()}
     */
    private void calculerSomme(){
        double somme = 0;
        for (int i = 0; i < this.numEigenvalues; i++) {
            somme += this.evdCache.getEigenvalue(i).real;
        }
        this.somme= somme;
    }

    /**
     * Trie les valeurs propres par ordre décroissant et les met en cache
     * dans {@link #valeursPropresTriees}
     * Doit être appelée après {@link #calculerValeursPropres()}
     */
    private void trierValeurPropre(){
        double[] valeurs = new double[this.numEigenvalues];
        for (int i = 0; i < this.numEigenvalues; i++) {
            valeurs[i] = this.evdCache.getEigenvalue(i).real;
        }
        Arrays.sort(valeurs);

        for (int i = 0; i < valeurs.length / 2; i++) {
            double temp = valeurs[i];
            valeurs[i] = valeurs[valeurs.length - 1 - i];
            valeurs[valeurs.length - 1 - i] = temp;
        }

        this.valeursPropresTriees = valeurs;
    }
}
