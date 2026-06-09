package systeme_reconnaissance;

import java.util.ArrayList;

/**
 * Une classe qui represente le systeme de reconnaissance faciale
 */
public class SystemeReconnaissance {
    /**
     * Le sous-espace
     */
    private SousEspace sousEspace;
    /**
     * Le seuil de reconnaissance a partir duquel on considere une personne inconnue. Il correspond a la distance max entre
     * 2 images lors de la reconnaissance
     */
    private double seuilReconnaissance;
    /**
     * Les personnes qui forment notre base de reconnaissance
     */
    private Personne[] personnes;

    /**
     * Projections des images d'apprentissage qu'on sauvegarde pour optimiser les benchamark
     */
    private Vecteur[] projectionsApprentissage;

    /**
     * Personne associee a chaque projection de {@link #projectionsApprentissage}
     */
    private Personne[] personnesApprentissage;

    /**
     * Une classe qui represente le systeme de reconnaissance faciale
     *
     * @param seuilReconnaissance Le seuil de reconnaissance a partir duquel on considere une personne inconnue. Il
     *                            correspond a la distance max entre 2 images lors de la reconnaissances
     * @param personnes           Les personnes qui forment notre base de reconnaissance
     */
    public SystemeReconnaissance(double seuilReconnaissance, Personne[] personnes) {
        this.sousEspace = null;
        this.seuilReconnaissance = seuilReconnaissance;
        this.personnes = personnes;
    }

    /**
     * Change la valeur de seuilReconnaissance
     * 
     * @param seuilReconnaissance La nouvelle valeur
     */
    public void setSeuilReconnaissance(double seuilReconnaissance) {
        this.seuilReconnaissance = seuilReconnaissance;
    }

    /**
     * Entraine le systeme sur la base de personnes
     */
    public void entrainer() {
        ArrayList<Vecteur> vecteurs = new ArrayList<>();
        ArrayList<Personne> proprietaires = new ArrayList<>(); // liste de personne qui sert à stocker les vecteurs qui represente l'image d'une personne

        for (int i = 0; i < personnes.length; i++) {
            for (Image image : personnes[i].getImages()) {
                vecteurs.add(image.toVecteur());
                proprietaires.add(personnes[i]);
            }
        }

        Vecteur[] vecteursArray = vecteurs.toArray(new Vecteur[0]);

        sousEspace = new SousEspace(vecteursArray);
        sousEspace.calculerEigenface(seuilReconnaissance);

        // On projette chaque image d'apprentissage une seule fois et on sauvegarde le resultat
        // pour que l'identification ne reprojette pas toute la base a chaque image testee.
        projectionsApprentissage = new Vecteur[vecteursArray.length];
        personnesApprentissage = proprietaires.toArray(new Personne[0]);
        for (int i = 0; i < vecteursArray.length; i++) {
            projectionsApprentissage[i] = sousEspace.projeter(vecteursArray[i]);
        }
    }

    /**
     * Lance le processus d'identification sur une image
     * 
     * @param imageTest L'image cible qu'on cherche a identifier
     * @return Une instance de ResultatIdentification qui contient les resultat de l'identification
     */
    public ResultatIdentification identifier(Image imageTest) {
        if (sousEspace == null) {
            throw new IllegalStateException("Le systeme doit etre entrainer avec de pouvoir faire une identification");
        }

        Vecteur projection = sousEspace.projeter(imageTest.toVecteur());
        int seuil = 3500;

        double min = Double.POSITIVE_INFINITY;
        Personne personneMin = null;

        for (int i = 0; i < projectionsApprentissage.length; i++) {
            double distance = projectionsApprentissage[i].distance(projection);

            if (distance < min) {
                min = distance;
                personneMin = personnesApprentissage[i];
            }
        }

        double erreur = calculerErreurReconstruction(imageTest.toVecteur(), sousEspace);
        System.out.print("Erreur : " + Math.round(erreur));

        boolean estReconnu = personneMin != null && min <= seuil;
        return new ResultatIdentification(personneMin, min, estReconnu);
    }

    double calculerErreurReconstruction(Vecteur vecteur, SousEspace sousEspace) {
        Vecteur projection = sousEspace.projeter(vecteur);
        Eigenface[] eigenfaces = sousEspace.getEigenfaces();

        // Reconstruction dans l'espace des pixels
        Vecteur reconstruction = sousEspace.getVecteurMoyen().copier();
        int dimension = reconstruction.getDimension();
        for (int i = 0; i < eigenfaces.length; i++) {
            double coordonnee = projection.getComposantesAvecIndex(i);
            Vecteur eigenface = eigenfaces[i].getVecteur();
            for (int p = 0; p < dimension; p++) {
                double valeur = reconstruction.getComposantesAvecIndex(p) + coordonnee * eigenface.getComposantesAvecIndex(p);
                reconstruction.setComposantesAvecIndex(p, valeur);
            }
        }

        return vecteur.distance(reconstruction);
    }

    /**
     * Calcule la statistique de Hotelling T2 d'une image, soit la somme des scores au carre divises
     * par la valeur propre associee
     *
     * @param vecteur    Le vecteur image a evaluer
     * @param sousEspace Le sous-espace des eigenfaces dans lequel projeter l'image
     * @return La statistique de Hotelling T2
     */
    double calculerStatistiqueHotelling(Vecteur vecteur, SousEspace sousEspace) {
        Vecteur projection = sousEspace.projeter(vecteur);
        Eigenface[] eigenfaces = sousEspace.getEigenfaces();

        double t2 = 0;
        for (int i = 0; i < eigenfaces.length; i++) {
            double beta = projection.getComposantesAvecIndex(i);
            double lambda = eigenfaces[i].getValeurPropre();
            if (lambda > 1.0e-12) {
                t2 += (beta * beta) / lambda;
            }
        }
        return t2;
    }

    /**
     * Calcule le seuil theorique de la statistique de Hotelling T2 avec la loi de Fisher
     * K(n-1)/(n-K) * F(K, n-K, niveauConfiance), n etant le nombre d'images d'apprentissage et K le
     * nombre d'eigenfaces. Un visage est coherent avec la base si sa statistique est inferieure a ce
     * seuil
     *
     * @param niveauConfiance Le niveau de confiance entre 0 et 1 (souvent 0.95 ou 0.99)
     * @return Le seuil de la statistique de Hotelling T2
     */
    double calculerSeuilHotelling(double niveauConfiance) {
        int n = projectionsApprentissage.length;
        int k = sousEspace.getEigenfaces().length;

        if (n - k <= 0 || k <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        double fisher = LoiFisher.quantile(niveauConfiance, k, n - k);
        return (k * (n - 1.0) / (n - k)) * fisher;
    }

    /**
     * Estime le seuil sur la distance minimale a partir des images d'apprentissage. Pour chaque
     * visage on prend la distance au plus proche voisin de la meme personne, puis on retient le
     * percentile choisi de ces distances. Un visage dont la distance minimale depasse ce seuil est
     * considere comme inconnu
     *
     * @param percentile La proportion choisie entre 0 et 1 (par exemple 0.95)
     * @return Le seuil sur la distance minimale
     */
    double calculerSeuilDistance(double percentile) {
        ArrayList<Double> distances = new ArrayList<>();

        for (int i = 0; i < projectionsApprentissage.length; i++) {
            double minMemePersonne = Double.POSITIVE_INFINITY;
            for (int j = 0; j < projectionsApprentissage.length; j++) {
                if (i == j || personnesApprentissage[i] != personnesApprentissage[j]) {
                    continue;
                }
                double distance = projectionsApprentissage[i].distance(projectionsApprentissage[j]);
                if (distance < minMemePersonne) {
                    minMemePersonne = distance;
                }
            }
            if (minMemePersonne != Double.POSITIVE_INFINITY) {
                distances.add(minMemePersonne);
            }
        }

        if (distances.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }

        double[] valeurs = new double[distances.size()];
        for (int i = 0; i < valeurs.length; i++) {
            valeurs[i] = distances.get(i);
        }
        java.util.Arrays.sort(valeurs);

        int indice = (int) Math.ceil(percentile * valeurs.length) - 1;
        if (indice < 0) {
            indice = 0;
        }
        if (indice >= valeurs.length) {
            indice = valeurs.length - 1;
        }
        return valeurs[indice];
    }
}
