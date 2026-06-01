package systeme_reconnaissance;

public class PixelCouleur extends Pixel{

    private int rouge;
    private int vert;
    private int bleu;

    PixelCouleur(int x, int y, int rouge, int vert, int bleu) {
        super(x, y);
        this.rouge = rouge;
        this.vert = vert;
        this.bleu = bleu;
    }

    public int getRouge() {
        return rouge;
    }

    public void setRouge(int rouge) {
        this.rouge = rouge;
    }

    public int getVert() {
        return vert;
    }

    public void setVert(int vert) {
        this.vert = vert;
    }

    public int getBleu() {
        return bleu;
    }

    public void setBleu(int bleu) {
        this.bleu = bleu;
    }
}
