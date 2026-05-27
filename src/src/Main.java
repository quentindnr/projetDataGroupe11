//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        Image image = new Image("image_test", "../archive/s1/1.pgm");
        Vecteur vecteur = image.toVecteur();
        System.out.println(image.toString());
        System.out.println(vecteur.toString());
        System.out.println("Nombre de dimensions du vecteur colonne : " + vecteur.getDimensions());

    }
}