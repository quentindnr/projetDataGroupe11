package systeme_reconnaissance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

/**
 * Une classe qui represente le sous-ensemble de projection
 */
public class SousEspace {
    private static final double VARIANCE_EXPLIQUEE_MIN = 0.95;
    private static final double EPSILON = 1e-9;
    /**
     * La matrice du sous-ensemble
     */
    private Matrice matrice;
    private Vecteur visageMoyen;
    private Matrice matriceCentree;
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

    public Vecteur getVisageMoyen() {
        return visageMoyen;
    }

    public Vecteur getVisageMoyen() {
        return visageMoyen;
    }

    /**
     * Vecteur[] vecteurs = new Vecteur[personnes.length];
     * 
     * for (int i = 0; i < personnes.length; i++) { vecteurs[i] = personnes[i].getImageIndice(0).toVecteur(); }
     * 
     * sousEspace = new SousEspace(vecteurs); sousEspace.calculerEigenface(seuilReconnaissance); Calcule les eigenfaces
     */
    public void calculerEigenface() {
        calculerEigenface(VARIANCE_EXPLIQUEE_MIN);
    }

    /**
     * Calcule les eigenfaces en conservant assez de composantes pour atteindre la variance expliquee cumulee demandee.
     *
     * @param varianceExpliqueeMin ratio entre 0 et 1
     */
    public void calculerEigenface(double varianceExpliqueeMin) {
        if (varianceExpliqueeMin <= 0 || varianceExpliqueeMin > 1) {
            throw new IllegalArgumentException("Le ratio de variance expliquee doit etre dans ]0, 1].");
        }

        visageMoyen = matrice.getVecteurMoyen();
        matriceCentree = matrice.getMatriceCentree();
        Matrice matriceCovariance = matriceCentree.getMatriceCovariance();
        SimpleEVD<SimpleMatrix> evd = matriceCovariance.eig();
        List<Integer> indices = new ArrayList<>();
        double sommeValeursPropres = 0;

        for (int i = 0; i < evd.getNumberOfEigenvalues(); i++) {
            double partieImaginaire = evd.getEigenvalue(i).imaginary;
            double valeurPropre = evd.getEigenvalue(i).real;
            if (Math.abs(partieImaginaire) <= EPSILON && valeurPropre > EPSILON && evd.getEigenVector(i) != null) {
                indices.add(i);
                sommeValeursPropres += valeurPropre;
            }
        }

        if (indices.isEmpty() || sommeValeursPropres <= EPSILON) {
            throw new IllegalStateException("Impossible de calculer les eigenfaces : variance nulle dans la base.");
        }

        indices.sort(Comparator.comparingDouble((Integer i) -> evd.getEigenvalue(i).real).reversed());
        List<Eigenface> composantes = new ArrayList<>();
        double varianceCumulee = 0;

        for (Integer indice : indices) {
            double valeurPropre = evd.getEigenvalue(indice).real;
            SimpleMatrix vecteurPropreReduit = evd.getEigenVector(indice);
            SimpleMatrix eigenfaceBrute = matriceCentree.mult(vecteurPropreReduit);
            double norme = eigenfaceBrute.normF();

            if (norme <= EPSILON) {
                continue;
            }

            SimpleMatrix eigenfaceNormalisee = eigenfaceBrute.scale(1.0 / norme);
            Eigenface eigenface = new Eigenface(valeurPropre, indice);
            eigenface.ajoutVecteur(toVecteur(eigenfaceNormalisee));
            composantes.add(eigenface);

            varianceCumulee += valeurPropre / sommeValeursPropres;
            if (varianceCumulee >= varianceExpliqueeMin) {
                break;
            }
        }

        if (composantes.isEmpty()) {
            throw new IllegalStateException("Impossible de calculer les eigenfaces normalisees.");
        }

        eigenfaces = composantes.toArray(new Eigenface[0]);
    }

    /**
     * Projete l'image sur ce sous-ensemble
     * 
     * @param image L'image a projeter
     */
    public Vecteur projeter(Image image) {
        if (eigenfaces == null || visageMoyen == null) {
            throw new IllegalStateException("Les eigenfaces doivent etre calculees avant de projeter une image.");
        }

        Vecteur imageCentree = image.toVecteur().copier();
        imageCentree.soustraire(visageMoyen);

        double[] coordonnees = new double[eigenfaces.length];
        for (int i = 0; i < eigenfaces.length; i++) {
            coordonnees[i] = imageCentree.produitScalaireSimple(eigenfaces[i].getVecteur());
        }

        return new Vecteur(coordonnees, coordonnees.length);
    }

    /**
     * Reconstruit une image a partir de la projection
     */
    public Image reconstruire(Vecteur projection) {
        if (eigenfaces == null || visageMoyen == null) {
            throw new IllegalStateException("Les eigenfaces doivent etre calculees avant de reconstruire une image.");
        }
        if (projection.getDimension() != eigenfaces.length) {
            throw new IllegalArgumentException("La projection ne correspond pas au nombre d'eigenfaces.");
        }

        double[] composantes = visageMoyen.copier().getComposantes();
        for (int i = 0; i < eigenfaces.length; i++) {
            double coefficient = projection.getComposantesAvecIndex(i);
            Vecteur eigenface = eigenfaces[i].getVecteur();
            for (int j = 0; j < composantes.length; j++) {
                composantes[j] += coefficient * eigenface.getComposantesAvecIndex(j);
            }
        }

        return Image.fromVecteur(new Vecteur(composantes, composantes.length), "reconstruction");
    }

    private Vecteur toVecteur(SimpleMatrix matriceColonne) {
        double[] composantes = new double[matriceColonne.getNumRows()];
        for (int i = 0; i < composantes.length; i++) {
            composantes[i] = matriceColonne.get(i, 0);
        }
        return new Vecteur(composantes, composantes.length);
    }
}
