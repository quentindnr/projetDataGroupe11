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
     * Calcule les eigenfaces
     */
    public void calculerEigenface(double seuil) {
        // centrer la matrice
        Matrice matriceCentree = matrice.getMatriceCentree();

        // calculer les valeurs propres
        Matrice matriceCovariance = matriceCentree.getMatriceCovariance();
        EVDCache valeursPropres = matriceCovariance.calculerValeursPropres();

        eigenfaces = valeursPropres.getComposantes(seuil);

        // retour a la matrice originale
        for (Eigenface eigenface : eigenfaces) {
            SimpleMatrix vecteur = eigenface.getVecteur().toSimpleMatrix();
            vecteur = matriceCentree.mult(vecteur);
            vecteur = vecteur.scale(1.0 / vecteur.normF());
            eigenface.ajoutVecteur(new Vecteur(vecteur));
        }
    }

    /**
     * Projete une image dans le sous-espace des eigenfaces.
     *
     * L'image est d'abord vectorisee et centree par soustraction du visage moyen, puis chaque coordonnee est obtenue par
     * produit scalaire avec une eigenface.
     *
     * @param image L'image a projeter
     * @return le gabarit de l'image dans le sous-espace
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