import java.io.File; /*Représenter un fichier sur le disque*/
import java.io.FileNotFoundException; /*Gérer l'erreur si fichier absent*/
import java.util.Scanner; /*Lire la console et les fichiers*/

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        /* Demande du chemin */
        System.out.print("Entrez le chemin de l'image PGM :");
        String path = scanner.nextLine();

        /* Vérification et lecture de l'en-tête PGM */
        try {
            Scanner fileScanner = new Scanner(new File(path));
            // Lire l'en-tête
            String type = fileScanner.nextLine();
            // Ignorer les commentaires
            String ligne = fileScanner.nextLine();
            while (ligne.startsWith("#")) {
                ligne = fileScanner.nextLine();
            }
        }catch (FileNotFoundException e) {
                System.out.println("Fichier introuvable : " + path);
        }

        /* Chargement de l'image */
        Image image = new Image("image_test");
        image.chargerImagePGM(path); /* lecture et travail sur un fichier pgm*/

        /* recuperation du seuil de tolerence */
        System.out.print("Seuil de tolerence compris entre 0 et 100");
        /*gestion erreur de saisie*/
        while (!scanner.hasNextInt()) {
            System.out.println("Erreur : veuillez entrer un entier compris entre 0 et 100.");
            scanner.next(); /*vider le buffer*/
        }
        while((scanner.nextInt() < 0) || (scanner.nextInt() > 100)){
            System.out.println("Erreur : veuillez entrer un entier compris entre 0 et 100.");
            scanner.next(); /*vider le buffer*/
        }
        int seuilT = scanner.nextInt();

        /* recuperation du repertoir de travail */
        File dossier = new File(System.getProperty("user.dir") + "/archive/");
        File[] fichiers = dossier.listFiles();

        /* verification de l'existance du dossier */
        if (fichiers == null) {
            System.out.println("Dossier archive introuvable");
            return;
        }

        /* Parcourir et charger chaque personne */
        Personne[] tabPersonne = new Personne[fichiers.length];
        int indice = 0;

        for (int i = 0; i < fichiers.length; i++) {
            if (fichiers[i].isDirectory()) {
                System.out.println("Chargement des image de " + fichiers[i].getName());
                tabPersonne[indice] = new Personne(fichiers[i].getName());
                tabPersonne[indice].getAllImagePersonne();
                indice++;
            }
        }
        scanner.close();
    }
}