package systeme_reconnaissance;

/**
 * Represente un pixel defini par ses coordonnees x et y.
 *
 * Cette classe est abstraite elle sert de base au classe PixelGris et PixelCouleur
 */
public abstract class Pixel {

    /** Coordonnee horizontale du pixel. */
    private int x;

    /** Coordonnee verticale du pixel. */
    private int y;

    /**
     * Construit un pixel a partir de ses coordonnees.
     *
     * @param x la coordonnee horizontale
     * @param y la coordonnee verticale
     */
    Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la coordonnee horizontale du pixel.
     *
     * @return la coordonnee x
     */
    public int getX() {
        return x;
    }

    /**
     * Modifie la coordonnee horizontale du pixel.
     *
     * @param x la nouvelle coordonnee x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Retourne la coordonnee verticale du pixel.
     *
     * @return la coordonnee y
     */
    public int getY() {
        return y;
    }

    /**
     * Modifie la coordonnee verticale du pixel.
     *
     * @param y la nouvelle coordonnee y
     */
    public void setY(int y) {
        this.y = y;
    }
}