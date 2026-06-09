package systeme_reconnaissance;

import java.util.Arrays;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

/**
 * Une classe qui represente une decomposition en valeurs propres
 */
public class EVDCache {
    /**
     * Cache de la décomposition en valeurs/vecteurs propres
     */
    private SimpleEVD<SimpleMatrix> evdCache;

    /**
     * La somme de toutes les valeurs propres, utilisee pour le calcul des variances
     */
    private double sommeValeursPropres;

    /**
     * Valeurs propres triees par ordre decroissant
     */
    private Eigenface[] valeursPropresTriees;

    /**
     * Une classe qui represente une decomposition en valeurs propres
     * 
     * @param evd La decomposition en valeurs propres
     */
    public EVDCache(SimpleEVD<SimpleMatrix> evd) {
        evdCache = evd;

        int numEigenvalues = evdCache.getNumberOfEigenvalues();

        sommeValeursPropres = 0;
        valeursPropresTriees = new Eigenface[numEigenvalues];

        for (int i = 0; i < numEigenvalues; i++) {
            double valeur = evdCache.getEigenvalue(i).real;
            valeursPropresTriees[i] = new Eigenface(valeur, i);
            sommeValeursPropres += valeur;
        }

        Arrays.sort(valeursPropresTriees, (Eigenface a, Eigenface b) -> Double.compare(b.getValeurPropre(), a.getValeurPropre()));
    }

    /**
     * Valeurs propres triees par ordre decroissant
     * 
     * @return Valeurs propres triees par ordre decroissant
     */
    public Eigenface[] getValeursPropresTriees() {
        return valeursPropresTriees;
    }

    /**
     * Calcule le vecteur propre d'une valeur propre donne des valeurs propres
     * 
     * @param rang Le rang de la valeur propre recherche
     * @return Le vecteur propre associe
     */
    public Vecteur getVecteurPropre(int rang) {
        SimpleMatrix eigenVector = evdCache.getEigenVector(rang);

        return new Vecteur(eigenVector);
    }

    /**
     * Calcule la variance expliquee a l'indice k Les valeurs propres sont triees par ordre decroissant
     *
     * @param k L'indice k de la valeur propre
     * @return La variance expliquee a l'indice k entre 0 et 1
     */
    public double calculerVarianceExplique(int k) {
        return valeursPropresTriees[k].getValeurPropre() / sommeValeursPropres;
    }

    /**
     * Calcule la variance expliquee cumulee jusqu'a l'indice k Les valeurs propres sont triees par ordre decroissant
     *
     * @param k L'indice k jusqu'auquel sommer les variances
     * @return La variance expliquee cumulee entre 0 et 1
     */
    public double calculerVarianceExpliqueCumule(int k) {
        double sommeV = 0;

        for (int i = 0; i < k; i++) {
            sommeV += valeursPropresTriees[i].getValeurPropre();
        }

        return sommeV / sommeValeursPropres;
    }

    /**
     * Calcule le nombre de composantes pricipales avec la methode du coude
     * 
     * @return Le nombre de composantes pricipales
     */
    public int calculerNbrComposantes(double seuil) {
        for (int i = 1; i <= valeursPropresTriees.length; i++) {
            if (calculerVarianceExpliqueCumule(i) >= seuil) {
                return i;
            }
        }
        return valeursPropresTriees.length;
    }

    /**
     * Les composantes pricipales
     * 
     * @return Les composantes pricipales
     */
    public Eigenface[] getComposantes(double seuil) {
        Eigenface[] composantes = Arrays.copyOfRange(valeursPropresTriees, 0, calculerNbrComposantes(seuil));

        for (Eigenface composante : composantes) {
            composante.ajoutVecteur(getVecteurPropre(composante.getRang()));
        }

        return composantes;
    }
}
