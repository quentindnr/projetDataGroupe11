package systeme_reconnaissance;

import org.ejml.simple.SimpleMatrix;

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
     * Les eigenfaces du sous-ensemble
     * 
     * @return Les eigenfaces du sous-ensemble
     */
    public Eigenface[] getEigenfaces() {
        return eigenfaces;
    }

    /**
     * Retourne le visage moyen du sous-espace.
     *
     * @return le vecteur moyen, dans l'espace des pixels
     */
    public Vecteur getVecteurMoyen() {
        return matrice.getVecteurMoyen();
    }

    /**
     * Calcule les eigenfaces du sous-espace : centre les donnees, calcule les valeurs propres et retient
     * les composantes principales jusqu'au seuil de variance cumulee
     *
     * @param seuil La proportion de variance cumulee a conserver (entre 0 et 1)
     */
    public void calculerEigenface(double seuil) {
        // centrer la matrice
        Matrice matriceCentree = matrice.getMatriceCentree();

        // calculer les valeurs propres
        Matrice matriceCovariance = matriceCentree.getMatriceCovariance();
        EVDCache valeursPropres = matriceCovariance.calculerValeursPropres();

        eigenfaces = valeursPropres.getComposantes(seuil);

        // Astuce de Turk-Pentland : les vecteurs propres v ont ete calcules sur la petite matrice
        // (1/m) A^T A. On remonte chaque eigenface dans l'espace des pixels par u = A * v, puis on la
        // normalise (norme 1) pour que les projections par produit scalaire restent correctes.
        for (Eigenface eigenface : eigenfaces) {
            SimpleMatrix vecteur = eigenface.getVecteur().toSimpleMatrix();
            vecteur = matriceCentree.mult(vecteur);
            vecteur = vecteur.scale(1.0 / vecteur.normF());
            eigenface.ajoutVecteur(new Vecteur(vecteur));
        }
    }

    /**
     * Projette un vecteur image dans le sous-espace des eigenfaces.
     *
     * Le vecteur est d'abord centre par soustraction du visage moyen, puis chaque coordonnee est obtenue par produit
     * scalaire avec une eigenface.
     *
     * @param vecteur le vecteur image a projeter
     * @return le gabarit du vecteur dans le sous-espace
     */
    public Vecteur projeter(Vecteur vecteur) {
        if (eigenfaces == null) {
            throw new IllegalStateException("Les eigenfaces doivent etre calculees avant de projeter une image.");
        }

        Vecteur imageCentree = vecteur.copier();
        imageCentree.soustraire(matrice.getVecteurMoyen());

        double[] coordonnees = new double[eigenfaces.length];
        for (int i = 0; i < eigenfaces.length; i++) {
            coordonnees[i] = imageCentree.produitScalaire(eigenfaces[i].getVecteur());
        }

        return new Vecteur(coordonnees, coordonnees.length);
    }
}