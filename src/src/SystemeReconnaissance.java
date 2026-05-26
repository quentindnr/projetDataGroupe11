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
     * Il correspond a la distance max entre 2 images apres l'acp
     */
    private float seuilReconnaissance;
    /**
     * Les personnes qui forment notre base de reconnaissance
     */
    private Personne[] personnes;

    /**
     * 
     * @param sousEspace          Le sous-espace
     * @param seuilReconnaissance Le seuil de reconnaissance a partir duquel on
     *                            considere une personne inconnue
     *                            Il correspond a la distance max entre 2 images
     *                            apres l'acp
     * @param personnes           Les personnes qui forment notre base de
     *                            reconnaissance
     */
    public SystemeReconnaissance(SousEspace sousEspace, float seuilReconnaissance, Personne[] personnes) {
        this.sousEspace = sousEspace;
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
     * Entraine une image
     * 
     * @param image L'image a entrainer
     */
    public void entrainer(Image image) {
        // TODO
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
    }

}
