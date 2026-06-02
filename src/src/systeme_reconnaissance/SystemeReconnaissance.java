package systeme_reconnaissance;

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
        // TODO
        // un truc de ce style
        // on initialise sous espace et on l'entraine (ie on construit la base / calc
        // les eigenfaces)

        Vecteur[] vecteurs = new Vecteur[personnes.length];

        for (int i = 0; i < personnes.length; i++) {
            vecteurs[i] = personnes[i].getImageIndice(0).toVecteur();
        }

        sousEspace = new SousEspace(vecteurs);
        sousEspace.calculerEigenface(seuilReconnaissance);
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

        Vecteur projection = sousEspace.projeter(imageTest);

        double min = seuilReconnaissance;
        Eigenface eigenfaceMin = null;

        for (Eigenface eigenface : sousEspace.getEigenfaces()) {
            double distance = eigenface.getVecteur().distance(projection);
            if (distance < min) {
                min = distance;
                eigenfaceMin = eigenface;
            }
        }

        Personne personne = personnes[eigenfaceMin.getRang()];

        return new ResultatIdentification(personne, min, min < seuilReconnaissance);
    }

}
