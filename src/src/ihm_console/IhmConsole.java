package ihm_console;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import systeme_reconnaissance.Image;
import systeme_reconnaissance.Personne;
import systeme_reconnaissance.ResultatIdentification;
import systeme_reconnaissance.SystemeReconnaissance;


public class IhmConsole {
    public static void main(String[] args) {

        String racine = System.getProperty("user.dir");
        File dossierTrain = new File(racine + "/archive/train/");
        File dossierTest = new File(racine + "/archive/test/");

        /* Chargement du data set d'entrainement */
        File[] dossiersTrain = dossierTrain.listFiles(File::isDirectory);
        if (dossiersTrain == null) {
            System.out.println("Erreur : Le dossier est introuvable.");
            return;
        }
        Arrays.sort(dossiersTrain, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        Personne[] tabPersonne = new Personne[dossiersTrain.length];
        Set<String> nomsConnus = new HashSet<>();
        int indice = 0;
        for (File f : dossiersTrain) {
            System.out.println("Chargement des images de " + f.getName());
            tabPersonne[indice] = new Personne(f.getName());
            tabPersonne[indice].chargerImagesDepuisDossier(f);
            nomsConnus.add(f.getName().toLowerCase());
            indice++;
        }

        /* 2. Entrainement du systeme */
        SystemeReconnaissance systeme = new SystemeReconnaissance(0.95, tabPersonne);
        System.out.println("\nEntrainement du systeme...");
        systeme.entrainer();

        /* 3. Parcours de toutes les images du dossier de test */
        File[] dossiersTest = dossierTest.listFiles(File::isDirectory);
        if (dossiersTest == null) {
            System.out.println("Erreur critique : Dossier '" + dossierTest.getPath() + "' introuvable.");
            return;
        }
        Arrays.sort(dossiersTest, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        int nbTotal = 0;
        int nbCorrect = 0;

        System.out.println("\n=== Lancement du benchmark sur le dossier de test ===\n");

        for (File dossierPersonne : dossiersTest) {
            String nomAttendu = dossierPersonne.getName();
            boolean estIntrus = !nomsConnus.contains(nomAttendu.toLowerCase());

            File[] images = dossierPersonne.listFiles(
                    fichier -> fichier.isFile() && fichier.getName().toLowerCase().endsWith(".pgm"));
            if (images == null) {
                continue;
            }
            Arrays.sort(images, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

            for (File fichierImage : images) {
                Image image = new Image(nomAttendu + "_test");
                image.chargerImagePGM(fichierImage.getPath());

                ResultatIdentification resultat = systeme.identifier(image);

                String nomPredit = resultat.estReconnu() && resultat.getPersonne() != null
                        ? resultat.getPersonne().getNom()
                        : "INCONNU";

                boolean correct;
                if (estIntrus) {
                    // Personne absente de la base : la bonne reponse est "non reconnu"
                    correct = !resultat.estReconnu();
                } else {
                    // Personne presente : la bonne reponse est le bon nom
                    correct = resultat.estReconnu() && nomPredit.equalsIgnoreCase(nomAttendu);
                }

                nbTotal++;
                if (correct) {
                    nbCorrect++;
                }

                System.out.printf("[%s] attendu=%-12s predit=%-7s distance=%.2f -> %s%n",
                        correct ? "OK " : "ERR",
                        estIntrus ? (nomAttendu + " (intrus)") : nomAttendu,
                        nomPredit, resultat.getDistance(),
                        correct ? "correct" : "incorrect");
            }
        }

        /* 4. Affichage du resultat final */
        double taux = nbTotal == 0 ? 0.0 : (100.0 * nbCorrect / nbTotal);
        System.out.println("\n=== Resultat du benchmark ===");
        System.out.printf("Bonnes reponses : %d / %d (%.2f%%)%n", nbCorrect, nbTotal, taux);
    }
}
