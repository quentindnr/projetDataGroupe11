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
     * Vecteurs de l'ensemble de validation, mis de cote pour estimer les seuils de la phase de robustesse
     */
    private Vecteur[] vecteursValidationArray;

    /**
     * Projections dans l'espace ACP des images de validation
     */
    private Vecteur[] projectionsValidation;

    /**
     * Personne associee a chaque image de validation
     */
    private Personne[] personnesValidationArray;

    /**
     * Personne associee a chaque projection de {@link #projectionsApprentissage}
     */
    private Personne[] personnesApprentissage;

    /**
     * Niveau de confiance utilise pour estimer les 3 seuils de la phase de robustesse (souvent 0.95 ou 0.99)
     */
    private static final double NIVEAU_CONFIANCE = 0.95;

    /**
     * Seuil sur la distance minimale au plus proche voisin (section 3.1)
     */
    private double seuilDistance;

    /**
     * Seuil sur l'erreur de reconstruction (section 3.2)
     */
    private double seuilReconstruction;

    /**
     * Seuil de la statistique de Hotelling T2 (section 3.3)
     */
    private double seuilHotelling;

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
     * Entraine le systeme sur la base de personnes. Une partie des images est mise de cote (ensemble
     * de validation) pour estimer les seuils de la phase de robustesse sur des visages non utilises
     * pour construire la base
     */
    public void entrainer() {
        ArrayList<Vecteur> vecteursBase = new ArrayList<>();
        ArrayList<Personne> personnesBase = new ArrayList<>();
        ArrayList<Vecteur> vecteursValidation = new ArrayList<>();
        ArrayList<Personne> personnesValidation = new ArrayList<>();

        for (int i = 0; i < personnes.length; i++) {
            Image[] images = personnes[i].getImages();
            // On reserve environ un quart des images de la personne pour la validation, en gardant
            // toujours au moins une image dans la base d'apprentissage.
            int nbValidation = images.length / 4;
            int debutValidation = images.length - nbValidation;
            for (int j = 0; j < images.length; j++) {
                Vecteur vecteur = images[j].toVecteur();
                if (j >= debutValidation && nbValidation > 0) {
                    vecteursValidation.add(vecteur);
                    personnesValidation.add(personnes[i]);
                } else {
                    vecteursBase.add(vecteur);
                    personnesBase.add(personnes[i]);
                }
            }
        }

        Vecteur[] vecteursArray = vecteursBase.toArray(new Vecteur[0]);

        sousEspace = new SousEspace(vecteursArray);
        sousEspace.calculerEigenface(seuilReconnaissance);

        // On projette chaque image d'apprentissage une seule fois et on sauvegarde le resultat
        // pour que l'identification ne reprojette pas toute la base a chaque image testee.
        projectionsApprentissage = new Vecteur[vecteursArray.length];
        personnesApprentissage = personnesBase.toArray(new Personne[0]);
        for (int i = 0; i < vecteursArray.length; i++) {
            projectionsApprentissage[i] = sousEspace.projeter(vecteursArray[i]);
        }

        // Projection de l'ensemble de validation (visages genuins non vus par la base).
        vecteursValidationArray = vecteursValidation.toArray(new Vecteur[0]);
        personnesValidationArray = personnesValidation.toArray(new Personne[0]);
        projectionsValidation = new Vecteur[vecteursValidationArray.length];
        for (int i = 0; i < vecteursValidationArray.length; i++) {
            projectionsValidation[i] = sousEspace.projeter(vecteursValidationArray[i]);
        }

        // Estimation des 3 seuils de la phase de robustesse (section 3.4) sur l'ensemble de
        // validation. Ils sont calcules une seule fois ici puis reutilises a chaque identification.
        seuilDistance = calculerSeuilDistance(NIVEAU_CONFIANCE);
        seuilReconstruction = calculerSeuilReconstruction(NIVEAU_CONFIANCE);
        seuilHotelling = calculerSeuilHotelling(NIVEAU_CONFIANCE);
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

        Vecteur vecteurTest = imageTest.toVecteur();
        Vecteur projection = sousEspace.projeter(vecteurTest);

        // Test de coherence du visage (section 3.4) : on verifie que le visage appartient a la meme
        // population que la base, via la statistique de Hotelling T2 et l'erreur de reconstruction
        double t2 = calculerStatistiqueHotelling(vecteurTest, sousEspace);
        double erreur = calculerErreurReconstruction(vecteurTest, sousEspace);
        boolean coherent = t2 < seuilHotelling && erreur < seuilReconstruction;

        // Distance au plus proche voisin de la base (section 3.1)
        double min = Double.POSITIVE_INFINITY;
        Personne personneMin = null;
        for (int i = 0; i < projectionsApprentissage.length; i++) {
            double distance = projectionsApprentissage[i].distance(projection);

            if (distance < min) {
                min = distance;
                personneMin = personnesApprentissage[i];
            }
        }

        // Decision finale (section 3.4) : le visage est reconnu si les tests de coherence sont valides
        // et que sa distance minimale reste sous le seuil, sinon il est rejete comme inconnu
        boolean estReconnu = coherent && personneMin != null && min < seuilDistance;
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
     * Estime le seuil sur l'erreur de reconstruction (section 3.2) a partir de l'ensemble de
     * validation. On calcule l'erreur de reconstruction de chaque visage de validation puis on
     * retient le percentile choisi (95-99 %) de ces erreurs. Un visage dont l'erreur de
     * reconstruction depasse ce seuil est considere comme atypique
     *
     * @param percentile La proportion choisie entre 0 et 1 (par exemple 0.95)
     * @return Le seuil sur l'erreur de reconstruction
     */
    double calculerSeuilReconstruction(double percentile) {
        if (vecteursValidationArray.length == 0) {
            return Double.POSITIVE_INFINITY;
        }

        double[] erreurs = new double[vecteursValidationArray.length];
        for (int i = 0; i < erreurs.length; i++) {
            erreurs[i] = calculerErreurReconstruction(vecteursValidationArray[i], sousEspace);
        }
        return percentile(erreurs, percentile);
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
     * Estime le seuil sur la distance minimale (section 3.1) a partir de l'ensemble de validation.
     * Pour chaque visage de validation, on calcule la distance au plus proche modele de la meme
     * personne dans la base, puis on retient le percentile choisi de ces distances. Un visage dont
     * la distance minimale depasse ce seuil est considere comme inconnu
     *
     * @param percentile La proportion choisie entre 0 et 1 (par exemple 0.95)
     * @return Le seuil sur la distance minimale
     */
    double calculerSeuilDistance(double percentile) {
        ArrayList<Double> distances = new ArrayList<>();

        for (int i = 0; i < projectionsValidation.length; i++) {
            double minMemePersonne = Double.POSITIVE_INFINITY;
            for (int j = 0; j < projectionsApprentissage.length; j++) {
                if (personnesValidationArray[i] != personnesApprentissage[j]) {
                    continue;
                }
                double distance = projectionsValidation[i].distance(projectionsApprentissage[j]);
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
        return percentile(valeurs, percentile);
    }

    /**
     * Retourne la valeur du tableau correspondant au percentile demande. Le tableau est trie en
     * place
     *
     * @param valeurs    Les valeurs observees
     * @param percentile La proportion choisie entre 0 et 1 (par exemple 0.95)
     * @return La valeur au percentile demande
     */
    private static double percentile(double[] valeurs, double percentile) {
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
