package systeme_reconnaissance;

import org.ejml.simple.SimpleMatrix;

/**
 * Représente un vecteur de dimension fixe.
 * 
 * Ce vecteur encapsule un tableau de composantes et fournit des opérations de base telles que l'ajout, la soustraction,
 * la multiplication composante à composante, le calcul du produit scalaire et de la distance.
 * 
 */
public class Vecteur {
    private double[] composantes;
    private int dimension;

    /**
     * Crée un vecteur à partir de ses composantes et de sa dimension.
     *
     * @param composantes le tableau des composantes du vecteur
     * @param dimension   la dimension du vecteur
     */
    public Vecteur(double[] composantes, int dimension) {
        this.composantes = composantes;
        this.dimension = dimension;
    }

    /**
     * Crée un vecteur à partir d'un vecteur colonne sous forme de matrice
     *
     * @param vecteurColonne Vecteur colonne sous forme de matrice
     * @param dimension      la dimension du vecteur
     */
    public Vecteur(SimpleMatrix vecteurColonne) {
        composantes = new double[vecteurColonne.getNumRows()];
        for (int i = 0; i < composantes.length; i++) {
            composantes[i] = vecteurColonne.get(i, 0);
        }
        this.dimension = composantes.length;
    }

    /**
     * Retourne la composante du vecteur à l'index spécifié.
     *
     * @param i l'index de la composante
     * @return la valeur de la composante
     */
    public double getComposantesAvecIndex(int i) {
        return composantes[i];
    }

    /**
     * Définit la composante du vecteur à l'index spécifié.
     *
     * @param i     l'index de la composante
     * @param value la nouvelle valeur de la composante
     */
    public void setComposantesAvecIndex(int i, double value) {
        composantes[i] = value;
    }

    /**
     * Retourne le tableau des composantes du vecteur.
     *
     * @return le tableau des composantes
     */
    public double[] getComposantes() {
        return this.composantes;
    }

    /**
     * Retourne la dimension du vecteur.
     *
     * @return la dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Définit la dimension du vecteur.
     *
     * @param dimension la nouvelle dimension
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * Ajoute les composantes d'un autre vecteur à ce vecteur.
     *
     * @param v le vecteur à ajouter
     * @throws IllegalArgumentException si les dimensions sont différentes
     */
    public void ajouter(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être ajoutés.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    /**
     * Soustrait les composantes d'un autre vecteur de ce vecteur.
     *
     * @param v le vecteur à soustraire
     * @throws IllegalArgumentException si les dimensions sont différentes
     */
    public void soustraire(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être soustraits.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    /**
     * Multiplie chaque composante de ce vecteur par la composante correspondante d'un autre vecteur.
     *
     * @param v le vecteur à multiplier
     * @throws IllegalArgumentException si les dimensions sont différentes
     */
    public void multiplier(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être multipliés.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] *= v.composantes[i];
        }
    }

    /**
     * Calcule le produit scalaire de ce vecteur avec un autre vecteur.
     *
     * @param v le vecteur avec lequel calculer le produit scalaire
     * @return une matrice de dimension x dimension contenant les produits des composantes
     * @throws IllegalArgumentException si les dimensions sont différentes
     */
    public double produitScalaire(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension.");
        }

        double resultat = 0;

        for (int i = 0; i < this.dimension; i++) {
            resultat += this.composantes[i] * v.composantes[i];
        }

        return resultat;
    }

    /**
     * Calcule la norme euclidienne du vecteur.
     *
     * @return la norme du vecteur
     */
    public int getNorme() {
        int sum = 0;
        for (int i = 0; i < this.dimension; i++) {
            sum += this.composantes[i] * this.composantes[i];
        }
        return (int) Math.sqrt(sum);
    }

    /**
     * Calcule la distance euclidienne entre ce vecteur et un autre vecteur.
     *
     * @param v le vecteur de comparaison
     * @return la distance entre les deux vecteurs
     * @throws IllegalArgumentException si les dimensions sont différentes
     */
    public double distance(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour calculer leur distance.");
        }
        int sum = 0;
        for (int i = 0; i < this.dimension; i++) {
            sum += Math.pow(this.composantes[i] - v.composantes[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Calcule le produit scalaire entre ce vecteur et un autre vecteur.
     *
     * @param v le vecteur de comparaison
     * @return la somme des produits composante par composante
     */
    public double produitScalaireSimple(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour calculer le produit scalaire.");
        }

        double somme = 0;
        for (int i = 0; i < this.dimension; i++) {
            somme += this.composantes[i] * v.composantes[i];
        }
        return somme;
    }

    /**
     * Cree une copie independante du vecteur.
     *
     * @return un nouveau vecteur avec les memes composantes
     */
    public Vecteur copier() {
        double[] copie = new double[this.dimension];
        System.arraycopy(this.composantes, 0, copie, 0, this.dimension);
        return new Vecteur(copie, this.dimension);
    }

    /**
     * Retourne une représentation textuelle du vecteur.
     *
     * @return une chaîne contenant les composantes séparées par des retours à la ligne
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.dimension - 1; i++) {
            stringBuilder.append(this.composantes[i]);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Transforme le Vecteur en SimpleMatrix
     * 
     * @return La SimpleMatrix
     */
    public SimpleMatrix toSimpleMatrix() {
        return new SimpleMatrix(composantes);
    }
}
