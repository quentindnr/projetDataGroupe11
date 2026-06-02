package systeme_reconnaissance;

/**
 * Classe qui représnte un pixel gris et qui hérite de la classe pixel
 */
public class PixelGris extends Pixel{

    /* Valeur du pixel en nuance de gris PGM.
    * C'est un entier compris entre 0 et 255*/
    private int nuanceGris;

    /**
     * Constructeur de la classe PixelGris qui permet d'initialisé une instance de PixelGris
     * @param x coordonnée horizontal du pixel
     * @param y coordonnée verticale du pixel
     * @param nuanceGris valeur (entre 0 et 255) du pixel
     */
    PixelGris(int x, int y, int nuanceGris) {
        super(x, y);
        this.nuanceGris = nuanceGris;
    }

    /**
     * Getter qui permet d'obtenir la valeur en nuance de gris de notre pixel
     * @return Un entier nuanceGris
     */
    public int getNuanceGris() {
        return nuanceGris;
    }

    /**
     * Setter qui permet de modifier la valeur de nuance de gris
     * @param nuanceGris entier qui représente la nouvelle valeur de nuanceGris (comprise entre 0 et 255)
     */
    public void setNuanceGris(int nuanceGris) {
        if((nuanceGris>=0) &&    (nuanceGris<=255)){
            this.nuanceGris = nuanceGris;
        }
    }
}
