import ihm_console.IhmConsole;
import ihm_graphique.IhmGraphique;

/**
 * Point d'entree de l'application. Lance par defaut l'interface graphique JavaFX (IhmGraphique).
 * La version console (IhmConsole) peut etre activee en decommentant l'appel correspondant.
 */
public class Main {
    /**
     * Lance l'application.
     *
     * @param args les arguments de la ligne de commande (non utilises)
     */
    public static void main(String[] args) {

        // version console
        // IhmConsole.main(args);

        // version javafx
        IhmGraphique.main(args);
    }
}