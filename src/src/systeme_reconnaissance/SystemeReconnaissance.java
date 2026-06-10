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
     * Ratio de variance expliquee cumulee a conserver pour choisir le nombre d'eigenfaces (phase 2).
     * Plus il est proche de 1, plus on retient de composantes principales
     */
    private double seuilVariance;
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
    private static final double NIVEAU_CONFIANCE = 0.99;

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
     * @param seuilVariance Ratio de variance expliquee cumulee a conserver pour choisir le nombre
     *                      d'eigenfaces (souvent 0.95). Plus il est proche de 1, plus on retient de
     *                      composantes principales
     * @param personnes     Les personnes qui forment notre base de reconnaissance
     */
    public SystemeReconnaissance(double seuilVariance, Personne[] personnes) {
        this.sousEspace = null;
        this.seuilVariance = seuilVariance;
        this.personnes = personnes;
    }

    /**
     * Change la valeur de seuilVariance
     *
     * @param seuilVariance La nouvelle valeur
     */
    public void setSeuilVariance(double seuilVariance) {
        this.seuilVariance = seuilVariance;
    }

    /**
     * Retourne le seuil sur la distance minimale Θd (section 3.1)
     *
     * @return le seuil sur la distance minimale
     */
    public double getSeuilDistance() {
        return seuilDistance;
    }

    /**
     * Retourne le seuil sur l'erreur de reconstruction Θr (section 3.2)
     *
     * @return le seuil sur l'erreur de reconstruction
     */
    public double getSeuilReconstruction() {
        return seuilReconstruction;
    }

    /**
     * Retourne le seuil de la statistique de Hotelling T2 (section 3.3)
     *
     * @return le seuil de Hotelling
     */
    public double getSeuilHotelling() {
        return seuilHotelling;
    }

    /**
     * Construit le sous-espace des eigenfaces et la galerie des projections de reference a partir
     * des vecteurs d'apprentissage fournis
     */
    private void construireGalerie(Vecteur[] vecteurs, Personne[] proprietaires) {
        sousEspace = new SousEspace(vecteurs);
        sousEspace.calculerEigenface(seuilVariance);

        // On projette chaque image d'apprentissage une seule fois et on sauvegarde le resultat
        // pour que l'identification ne reprojette pas toute la base a chaque image testee.
        projectionsApprentissage = new Vecteur[vecteurs.length];
        personnesApprentissage = proprietaires;
        for (int i = 0; i < vecteurs.length; i++) {
            projectionsApprentissage[i] = sousEspace.projeter(vecteurs[i]);
        }
    }

    /**
     * Enregistre l'ensemble de validation (visages connus non utilises pour construire la base) et
     * en deduit les 3 seuils de la phase de robustesse (section 3.4). Si des intrus de validation
     * sont fournis, le seuil de distance est choisi entre les deux distributions (section 3.1)
     */
    private void estimerSeuils(Vecteur[] vecteursValidation, Personne[] personnesValidation, Vecteur[] intrusValidation) {
        vecteursValidationArray = vecteursValidation;
        personnesValidationArray = personnesValidation;
        projectionsValidation = new Vecteur[vecteursValidation.length];
        for (int i = 0; i < vecteursValidation.length; i++) {
            projectionsValidation[i] = sousEspace.projeter(vecteursValidation[i]);
        }

        seuilReconstruction = calculerSeuilReconstruction(NIVEAU_CONFIANCE);
        seuilHotelling = calculerSeuilHotelling(NIVEAU_CONFIANCE);
        if (intrusValidation != null && intrusValidation.length > 0) {
            seuilDistance = calculerSeuilDistanceDiscriminant(intrusValidation);
        } else {
            seuilDistance = calculerSeuilDistance(NIVEAU_CONFIANCE);
        }
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

        construireGalerie(vecteursBase.toArray(new Vecteur[0]), personnesBase.toArray(new Personne[0]));
        estimerSeuils(vecteursValidation.toArray(new Vecteur[0]), personnesValidation.toArray(new Personne[0]), null);
    }

    /**
     * Entraine le systeme avec une base de validation externe (methodologie a trois bases :
     * apprentissage / validation / test). La galerie est construite sur la totalite des images de
     * reference, et les 3 seuils sont estimes sur la base de validation.
     *
     * @param validationConnus Les visages connus de la base de validation (memes identites que la
     *                         base de reference, mais d'autres images)
     * @param intrusValidation Les visages inconnus de la base de validation, utilises pour choisir
     *                         le seuil de distance entre les deux distributions (section 3.1)
     */
    public void entrainer(Personne[] validationConnus, Vecteur[] intrusValidation) {
        ArrayList<Vecteur> vecteursBase = new ArrayList<>();
        ArrayList<Personne> personnesBase = new ArrayList<>();
        for (int i = 0; i < personnes.length; i++) {
            for (Image image : personnes[i].getImages()) {
                vecteursBase.add(image.toVecteur());
                personnesBase.add(personnes[i]);
            }
        }
        construireGalerie(vecteursBase.toArray(new Vecteur[0]), personnesBase.toArray(new Personne[0]));

        ArrayList<Vecteur> vecteursValidation = new ArrayList<>();
        ArrayList<Personne> personnesValidation = new ArrayList<>();
        for (int i = 0; i < validationConnus.length; i++) {
            for (Image image : validationConnus[i].getImages()) {
                vecteursValidation.add(image.toVecteur());
                personnesValidation.add(validationConnus[i]);
            }
        }
        estimerSeuils(vecteursValidation.toArray(new Vecteur[0]), personnesValidation.toArray(new Personne[0]), intrusValidation);
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
                if (!personnesValidationArray[i].getNom().equals(personnesApprentissage[j].getNom())) {
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
     * Choisit le seuil sur la distance minimale (section 3.1) "entre les deux distributions" en
     * utilisant les intrus de la base de validation. On calcule, pour chaque visage connu de
     * validation, la distance au plus proche modele de la meme personne, et pour chaque intrus la
     * distance au plus proche modele quelconque. Le seuil retenu est celui qui minimise le nombre
     * d'erreurs sur la validation (connus rejetes + intrus acceptes)
     *
     * @param intrusValidation Les vecteurs des visages inconnus de la base de validation
     * @return Le seuil sur la distance minimale
     */
    double calculerSeuilDistanceDiscriminant(Vecteur[] intrusValidation) {
        ArrayList<Double> connus = new ArrayList<>();
        for (int i = 0; i < projectionsValidation.length; i++) {
            double minMemePersonne = Double.POSITIVE_INFINITY;
            for (int j = 0; j < projectionsApprentissage.length; j++) {
                if (!personnesValidationArray[i].getNom().equals(personnesApprentissage[j].getNom())) {
                    continue;
                }
                double distance = projectionsValidation[i].distance(projectionsApprentissage[j]);
                if (distance < minMemePersonne) {
                    minMemePersonne = distance;
                }
            }
            if (minMemePersonne != Double.POSITIVE_INFINITY) {
                connus.add(minMemePersonne);
            }
        }

        ArrayList<Double> intrus = new ArrayList<>();
        for (Vecteur vecteur : intrusValidation) {
            Vecteur projection = sousEspace.projeter(vecteur);
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < projectionsApprentissage.length; j++) {
                double distance = projection.distance(projectionsApprentissage[j]);
                if (distance < min) {
                    min = distance;
                }
            }
            intrus.add(min);
        }

        if (connus.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        if (intrus.isEmpty()) {
            return calculerSeuilDistance(NIVEAU_CONFIANCE);
        }

        // On teste chaque distance observee comme seuil candidat et on garde celui qui minimise les
        // erreurs : un visage connu a une distance >= seuil est rejete a tort, un intrus a une
        // distance < seuil est accepte a tort.
        ArrayList<Double> candidats = new ArrayList<>(connus);
        candidats.addAll(intrus);

        double meilleurSeuil = candidats.get(0);
        int meilleurErreurs = Integer.MAX_VALUE;
        for (double seuil : candidats) {
            int erreurs = 0;
            for (double d : connus) {
                if (d >= seuil) {
                    erreurs++;
                }
            }
            for (double d : intrus) {
                if (d < seuil) {
                    erreurs++;
                }
            }
            if (erreurs < meilleurErreurs) {
                meilleurErreurs = erreurs;
                meilleurSeuil = seuil;
            }
        }
        return meilleurSeuil;
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
