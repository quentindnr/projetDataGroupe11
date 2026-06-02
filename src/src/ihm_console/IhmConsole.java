package ihm_console;

import java.io.File;
import java.util.Scanner;

import systeme_reconnaissance.Image;
import systeme_reconnaissance.Personne;

public class IhmConsole {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String path = "";
        boolean fichierValide = false;

        /* 1. Demande du chemin en boucle jusqu'à validation stricte */
        while (!fichierValide) {
            System.out.print("Entrez le chemin de l'image PGM : ");
            path = scanner.nextLine();
            File pgmFile = new File(path);

            // Vérifier si c'est un fichier existant
            if (!pgmFile.exists() || !pgmFile.isFile()) {
                System.out.println("Erreur : Fichier introuvable. Veuillez réessayer.");
                continue;
            }

            // Vérifier si le fichier est physiquement vide
            if (pgmFile.length() == 0) {
                System.out.println("Erreur : Le fichier est physiquement vide (0 octet).");
                continue;
            }

            // Vérification binaire de l'en-tête (Magic Number P2 ou P5)
            try (java.io.FileInputStream fis = new java.io.FileInputStream(pgmFile)) {
                int byte1 = fis.read();
                int byte2 = fis.read();

                // On vérifie que les deux premiers octets forment "P2" (ASCII) ou "P5" (Binaire)
                if (byte1 == 'P' && (byte2 == '2' || byte2 == '5')) {
                    fichierValide = true;
                } else {
                    System.out.println("Erreur : Le fichier n'est pas un PGM valide (doit commencer par P2 ou P5).");
                }
            } catch (java.io.IOException e) {
                System.out.println("Erreur système : Impossible de lire les octets du fichier.");
            }
        }

        /* 2. Chargement de l'image (exécution sécurisée) */
        Image image = new Image("image_test");
        image.chargerImagePGM(path);

        /* 3. Récupération du seuil de tolérance (logique corrigée) */
        float seuilT = -1;
        System.out.print("Seuil de tolérance compris entre 0 et 1 : ");

        while (true) {
            if (!scanner.hasNextFloat()) {
                System.out.println("Erreur : vous devez entrer un nombre à virgule (exemple 0.90).");
                scanner.next();
            } else {
                seuilT = scanner.nextFloat();
                if (seuilT >= 0 && seuilT <= 1) {
                    break;
                } else {
                    System.out.println("Erreur : veuillez entrer un nombre à virgule compris entre 0 et 1.");
                }
            }
        }
        scanner.nextLine(); /* Vider le retour chariot restant après nextInt() */

        /* 4. Récupération du répertoire de travail */
        File dossier = new File(System.getProperty("user.dir") + "/archive/");
        File[] fichiers = dossier.listFiles();

        /* Vérification de l'existence du dossier */
        if (fichiers == null) {
            System.out.println("Erreur critique : Dossier '/archive/' introuvable.");
            scanner.close();
            return;
        }

        /* 5. Parcourir et charger chaque personne (Allocation mémoire optimisée) */
        // Compter les dossiers réels pour allouer la bonne taille au tableau
        int nbDossiers = 0;
        for (File f : fichiers) {
            if (f.isDirectory()) nbDossiers++;
        }

        Personne[] tabPersonne = new Personne[nbDossiers];
        int indice = 0;

        for (File f : fichiers) {
            if (f.isDirectory()) {
                System.out.println("Chargement des images de " + f.getName());
                tabPersonne[indice] = new Personne(f.getName());
                tabPersonne[indice].getAllImagePersonne();
                indice++;
            }
        }

        scanner.close();
    }
}
