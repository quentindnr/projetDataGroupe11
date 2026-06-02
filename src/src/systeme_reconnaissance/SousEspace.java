package systeme_reconnaissance;

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
    }

    /**
     * Projete l'image sur ce sous-ensemble
     * 
     * @param image L'image a projeter
     */
    public Vecteur projeter(Image image) {
        // TODO
        return new Vecteur(null, 0);
    }

    /**
     * Reconstruit une image a partir de la projection
     */
    public Image reconstruire(Vecteur projection) {
        // TODO
        return new Image(null);
    }
}
