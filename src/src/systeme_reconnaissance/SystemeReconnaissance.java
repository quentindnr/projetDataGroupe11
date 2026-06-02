package systeme_reconnaissance;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /** Projections des images d'apprentissage conservees pour l'identification. */
    private Gabarit[] gabarits;

    /**
     * Associe une image d'apprentissage, sa personne et sa projection dans le sous-espace.
     */
    private static class Gabarit {
        /** Personne a laquelle appartient l'image d'apprentissage. */
        private final Personne personne;

        /** Image d'apprentissage utilisee pour creer ce gabarit. */
        private final Image image;

        /** Coordonnees de l'image dans le sous-espace des eigenfaces. */
        private final Vecteur projection;

        /**
         * Cree un gabarit de reconnaissance.
         *
         * @param personne   la personne associee a l'image
         * @param image      l'image d'apprentissage
         * @param projection la projection de l'image dans le sous-espace
         */
        private Gabarit(Personne personne, Image image, Vecteur projection) {
            this.personne = personne;
            this.image = image;
            this.projection = projection;
        }
    }

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
        this.gabarits = new Gabarit[0];
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
        if (personnes == null || personnes.length == 0) {
            throw new IllegalStateException("La base d'apprentissage est vide.");
        }

        List<Vecteur> vecteurs = new ArrayList<>();
        List<Personne> proprietaires = new ArrayList<>();
        List<Image> images = new ArrayList<>();

        for (Personne personne : personnes) {
            if (personne.getNombreImages() == 0) {
                personne.getAllImagePersonne();
            }
            for (Image image : personne.getImages()) {
                vecteurs.add(image.toVecteur());
                proprietaires.add(personne);
                images.add(image);
            }
        }

        if (vecteurs.isEmpty()) {
            throw new IllegalStateException("Aucune image PGM chargee pour entrainer le systeme.");
        }

        sousEspace = new SousEspace(vecteurs.toArray(new Vecteur[0]));
        sousEspace.calculerEigenface();
        gabarits = new Gabarit[images.size()];

        for (int i = 0; i < images.size(); i++) {
            gabarits[i] = new Gabarit(proprietaires.get(i), images.get(i), sousEspace.projeter(images.get(i)));
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

        Vecteur projection = sousEspace.projeter(imageTest);

        double min = Double.POSITIVE_INFINITY;
        Gabarit gabaritMin = null;

        for (Gabarit gabarit : gabarits) {
            double distance = gabarit.projection.distance(projection);
            if (distance < min) {
                min = distance;
                gabaritMin = gabarit;
            }
        }

        boolean estReconnu = gabaritMin != null && min <= seuilReconnaissance;
        return new ResultatIdentification(estReconnu ? gabaritMin.personne : null, min, estReconnu);
    }

    /**
     * Retourne le sous-espace calcule pendant l'entrainement.
     *
     * @return le sous-espace de projection, ou null si le systeme n'est pas encore entraine
     */
    public SousEspace getSousEspace() {
        return sousEspace;
    }

    /**
     * Retourne le nombre de gabarits disponibles dans la base de reconnaissance.
     *
     * @return le nombre d'images d'apprentissage projetees
     */
    public int getNombreGabarits() {
        return gabarits.length;
    }

    /**
     * Charge toutes les personnes presentes dans un dossier racine.
     *
     * Chaque sous-dossier est considere comme une personne et les fichiers PGM qu'il contient
     * sont charges comme images d'apprentissage.
     *
     * @param cheminDossierRacine le chemin du dossier contenant les sous-dossiers de personnes
     * @return le tableau des personnes chargees
     */
    public static Personne[] chargerPersonnesDepuisDossier(String cheminDossierRacine) {
        File dossierRacine = new File(cheminDossierRacine);
        File[] dossiers = dossierRacine.listFiles(File::isDirectory);

        if (dossiers == null) {
            throw new IllegalArgumentException("Dossier racine introuvable : " + cheminDossierRacine);
        }

        Arrays.sort(dossiers, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        Personne[] personnes = new Personne[dossiers.length];

        for (int i = 0; i < dossiers.length; i++) {
            Personne personne = new Personne(dossiers[i].getName());
            personne.chargerImagesDepuisDossier(dossiers[i]);
            personnes[i] = personne;
        }

        return personnes;
    }

}
