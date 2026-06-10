package systeme_reconnaissance;

/**
 * Classe qui représente un pixel de couleur (RGB) et qui hérite de la classe Pixel.
 */
public class PixelCouleur extends Pixel {

    /** Entier qui représente la nuance de rouge du pixel. */
    private int rouge;

    /** Entier qui représente la nuance de vert du pixel. */
    private int vert;

    /** Entier qui représente la nuance de bleu du pixel. */
    private int bleu;

    /**
     * Constructeur de la classe PixelCouleur qui permet d'initialiser une instance.
     *
     * @param x     Entier qui représente la coordonnée horizontale du pixel.
     * @param y     Entier qui représente la coordonnée verticale du pixel.
     * @param rouge Entier qui représente la nuance de rouge du pixel.
     * @param vert  Entier qui représente la nuance de vert du pixel.
     * @param bleu  Entier qui représente la nuance de bleu du pixel.
     */
    public PixelCouleur(int x, int y, int rouge, int vert, int bleu) {
        super(x, y);
        this.rouge = rouge;
        this.vert = vert;
        this.bleu = bleu;
    }

    /**
     * Récupère la valeur de la nuance de rouge du pixel.
     *
     * @return L'entier représentant la nuance de rouge.
     */
    public int getRouge() {
        return rouge;
    }

    /**
     * Modifie la valeur de la nuance de rouge du pixel.
     *
     * @param rouge La nouvelle valeur de la nuance de rouge.
     */
    public void setRouge(int rouge) {
        this.rouge = rouge;
    }

    /**
     * Récupère la valeur de la nuance de vert du pixel.
     *
     * @return L'entier représentant la nuance de vert.
     */
    public int getVert() {
        return vert;
    }

    /**
     * Modifie la valeur de la nuance de vert du pixel.
     *
     * @param vert La nouvelle valeur de la nuance de vert.
     */
    public void setVert(int vert) {
        this.vert = vert;
    }

    /**
     * Récupère la valeur de la nuance de bleu du pixel.
     *
     * @return L'entier représentant la nuance de bleu.
     */
    public int getBleu() {
        return bleu;
    }

    /**
     * Modifie la valeur de la nuance de bleu du pixel.
     *
     * @param bleu La nouvelle valeur de la nuance de bleu.
     */
    public void setBleu(int bleu) {
        this.bleu = bleu;
    }
}
