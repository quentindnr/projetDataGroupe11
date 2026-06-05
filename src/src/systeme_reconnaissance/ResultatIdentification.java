package systeme_reconnaissance;

/**
 * Une classe qui represente le résultat d'une tentative d'identification faciale.
 */
public class ResultatIdentification {

    private Personne personne;
    private double distance;
    private boolean estReconnu;

    /**
     * Crée un nouveau résultat d'identification. * @param personne La personne identifiée (peut être null si non reconnu).
     * 
     * @param distance   La distance euclidienne calculée par l'algorithme ACP.
     * @param estReconnu true si la distance est inférieure au seuil de tolérance, false sinon.
     */
    public ResultatIdentification(Personne personne, double distance, boolean estReconnu) {
        this.personne = personne;
        this.distance = distance;
        this.estReconnu = estReconnu;
    }


    /**
     * Indique si le visage a été reconnu.
     * 
     * @return true si reconnu, false sinon.
     */
    public boolean estReconnu() {
        return this.estReconnu;
    }

    /**
     * Récupère la personne identifiée.
     * 
     * @return L'objet Personne correspondant, ou null si le visage est inconnu.
     */
    public Personne getPersonne() {
        return this.personne;
    }

    /**
     * Récupère la distance calculée par le système.
     * 
     * @return La distance en format double.
     */
    public double getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        if (estReconnu) {
            return "Visage reconnu : " + personne.getNom() + " (Distance: " + distance + ")";
        } else {
            return "Visage inconnu (Distance: " + distance + ")";
        }
    }
}