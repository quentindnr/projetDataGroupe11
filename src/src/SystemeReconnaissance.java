/**
 * Une classe qui represente le systeme de reconnaissance faciale
 */
public class SystemeReconnaissance {
    /**
     * Le sous-espace
     */
    private SousEspace sousEspace;
    /**
     * Le seuil de reconnaissance a partir duquel on considere une personne inconnue
     * Il correspond a la distance max entre 2 images lors de la reconnaissance
     */
    private float seuilReconnaissance;
    /**
     * Les personnes qui forment notre base de reconnaissance
     */
    private Personne[] personnes;

    /**
     * Une classe qui represente le systeme de reconnaissance faciale
     * 
     * @param seuilReconnaissance Le seuil de reconnaissance a partir duquel on
     *                            considere une personne inconnue
     *                            Il correspond a la distance max entre 2 images
     *                            lors de la reconnaissances
     * @param personnes           Les personnes qui forment notre base de
     *                            reconnaissance
     */
    public SystemeReconnaissance(float seuilReconnaissance, Personne[] personnes) {
        this.sousEspace = null;
        this.seuilReconnaissance = seuilReconnaissance;
        this.personnes = personnes;
    }

    /**
     * Change la valeur de seuilReconnaissance
     * 
     * @param seuilReconnaissance La nouvelle valeur
     */
    public void setSeuilReconnaissance(float seuilReconnaissance) {
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
            vecteurs[i] = personnes[i].getImage().toVecteur();
        }

        sousEspace = new SousEspace(vecteurs);
        sousEspace.calculerEigenface();
    }

    /**
     * Lance le processus d'identification sur une image
     * 
     * @param imageTest L'image cible qu'on cherche a identifier
     * @return Une instance de ResultatIdentification qui contient les resultat de
     *         l'identification
     */
    public ResultatIdentification identifier(Image imageTest) {
        // TODO
        // avant tout, faut check si sousEspace est non null (ie le model est entraine)
        return null;
    }

}
