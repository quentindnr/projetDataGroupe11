package ihm_console;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import systeme_reconnaissance.Image;
import systeme_reconnaissance.Personne;
import systeme_reconnaissance.ResultatIdentification;
import systeme_reconnaissance.SystemeReconnaissance;
import systeme_reconnaissance.Vecteur;


/**
 * Pilote console du systeme suivant la methodologie a trois bases.
 *
 * La base d'apprentissage (archive/train) sert a construire l'ACP, la base de validation
 * (archive/validation) a calibrer les seuils de la phase de robustesse, et la base de test
 * (archive/test) a mesurer la performance finale sans avoir servi ni a l'apprentissage ni au reglage.
 */
public class IhmConsole {
    public static void main(String[] args) {

        String racine = System.getProperty("user.dir");
        File dossierTrain = new File(racine + "/archive/train/");
        File dossierValidation = new File(racine + "/archive/validation/");
        File dossierTest = new File(racine + "/archive/test/");

        // Base d'apprentissage : toutes les personnes connues
        Personne[] tabPersonne = chargerPersonnes(dossierTrain);
        if (tabPersonne == null) {
            System.out.println("Erreur : dossier d'apprentissage introuvable.");
            return;
        }
        Set<String> nomsConnus = new HashSet<>();
        for (Personne personne : tabPersonne) {
            nomsConnus.add(personne.getNom().toLowerCase());
        }

        // Base de validation : on separe les visages connus (memes identites que l'apprentissage)
        // des intrus (identites absentes de l'apprentissage) servant a regler le seuil de distance
        Personne[] validationToutes = chargerPersonnes(dossierValidation);
        if (validationToutes == null) {
            System.out.println("Erreur : dossier de validation introuvable.");
            return;
        }
        ArrayList<Personne> validationConnus = new ArrayList<>();
        ArrayList<Vecteur> intrusValidation = new ArrayList<>();
        for (Personne personne : validationToutes) {
            if (nomsConnus.contains(personne.getNom().toLowerCase())) {
                validationConnus.add(personne);
            } else {
                for (Image image : personne.getImages()) {
                    intrusValidation.add(image.toVecteur());
                }
            }
        }

        // Entrainement et calibration des seuils sur la base de validation
        SystemeReconnaissance systeme = new SystemeReconnaissance(0.95, tabPersonne);
        System.out.println("Entrainement et calibration sur la base de validation...");
        systeme.entrainer(validationConnus.toArray(new Personne[0]), intrusValidation.toArray(new Vecteur[0]));
        System.out.printf("Seuils calibres : Distance=%.2f  Reconstruction=%.2f  Hotelling=%.2f%n",
                systeme.getSeuilDistance(), systeme.getSeuilReconstruction(), systeme.getSeuilHotelling());

        // Base de test : mesure de la performance finale
        Personne[] dossiersTest = chargerPersonnes(dossierTest);
        if (dossiersTest == null) {
            System.out.println("Erreur critique : dossier '" + dossierTest.getPath() + "' introuvable.");
            return;
        }

        int nbTotal = 0;
        int nbCorrect = 0;
        int connusOk = 0;
        int connusRejetes = 0;
        int connusMauvaiseId = 0;
        int nbConnus = 0;
        int intrusOk = 0;
        int intrusAcceptes = 0;
        int nbIntrus = 0;

        System.out.println("\n=== Lancement du benchmark sur la base de test ===\n");

        for (Personne personne : dossiersTest) {
            String nomAttendu = personne.getNom();
            boolean estIntrus = !nomsConnus.contains(nomAttendu.toLowerCase());

            for (Image image : personne.getImages()) {
                ResultatIdentification resultat = systeme.identifier(image);

                String nomPredit = resultat.estReconnu() && resultat.getPersonne() != null
                        ? resultat.getPersonne().getNom()
                        : "INCONNU";

                boolean correct;
                if (estIntrus) {
                    correct = !resultat.estReconnu();
                    nbIntrus++;
                    if (correct) {
                        intrusOk++;
                    } else {
                        intrusAcceptes++;
                    }
                } else {
                    correct = resultat.estReconnu() && nomPredit.equalsIgnoreCase(nomAttendu);
                    nbConnus++;
                    if (correct) {
                        connusOk++;
                    } else if (!resultat.estReconnu()) {
                        connusRejetes++;
                    } else {
                        connusMauvaiseId++;
                    }
                }

                nbTotal++;
                if (correct) {
                    nbCorrect++;
                }
                System.out.printf(" %s attendu=%-12s predit=%-7s distance=%.2f -> %s%n",
                        correct ? "OK " : "ERR",
                        estIntrus ? (nomAttendu + " (intrus)") : nomAttendu,
                        nomPredit, resultat.getDistance(),
                        correct ? "correct" : "incorrect");
            }
        }

        // Resultat final
        double taux = nbTotal == 0 ? 0.0 : (100.0 * nbCorrect / nbTotal);
        System.out.println("\n=== Resultat du benchmark sur la base de test ===");
        System.out.printf("Taux d'identification global T = %d/%d = %.2f%%%n", nbCorrect, nbTotal, taux);
        if (nbConnus > 0) {
            System.out.printf("Connus bien identifies   : %d/%d (%.2f%%)%n",
                    connusOk, nbConnus, 100.0 * connusOk / nbConnus);
            System.out.printf("   - rejetes a tort       : %d%n", connusRejetes);
            System.out.printf("   - mauvaise identite    : %d%n", connusMauvaiseId);
        }
        if (nbIntrus > 0) {
            System.out.printf("Intrus bien rejetes      : %d/%d (%.2f%%)%n",
                    intrusOk, nbIntrus, 100.0 * intrusOk / nbIntrus);
            System.out.printf("   - acceptes a tort      : %d%n", intrusAcceptes);
        }
    }

    /**
     * Charge toutes les personnes (un sous-dossier par personne) d'un dossier de base.
     *
     * @param dossier le dossier contenant un sous-dossier par personne
     * @return les personnes triees par nom, ou null si le dossier est introuvable
     */
    private static Personne[] chargerPersonnes(File dossier) {
        File[] sousDossiers = dossier.listFiles(File::isDirectory);
        if (sousDossiers == null) {
            return null;
        }
        Arrays.sort(sousDossiers, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        Personne[] personnes = new Personne[sousDossiers.length];
        for (int i = 0; i < sousDossiers.length; i++) {
            personnes[i] = new Personne(sousDossiers[i].getName());
            personnes[i].chargerImagesDepuisDossier(sousDossiers[i]);
        }
        return personnes;
    }
}
