import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {
    public static void main(String[] args) {

        File dossier = new File(System.getProperty("user.dir") + "/archive/");
        File[] fichiers = dossier.listFiles();

        if (fichiers == null) {
            System.out.println("Dossier archive introuvable");
            return;
        }

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
    }
}