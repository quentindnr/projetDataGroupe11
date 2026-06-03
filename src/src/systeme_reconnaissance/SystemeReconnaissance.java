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

        boolean estReconnu = personneMin != null && min <= seuil;
        return new ResultatIdentification(personneMin, min, estReconnu);
    }
}