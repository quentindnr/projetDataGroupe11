package systeme_reconnaissance;

public class PixelGris extends Pixel{

    private int nuanceGris;

    PixelGris(int x, int y, int nuanceGris) {
        super(x, y);
        this.nuanceGris = nuanceGris;
    }

    public int getNuanceGris() {
        return nuanceGris;
    }

    public void setNuanceGris(int nuanceGris) {
        this.nuanceGris = nuanceGris;
    }
}
