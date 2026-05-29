//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/archive/test.pgm";
        String path2 = System.getProperty("user.dir") + "/archive/test.pgm";
        System.out.println("Hello World");
        Image image = new Image("image_test");
        image.chargerImage(path);
        Image image2 = new Image("Image_test2");
        image2.chargerImage(path2);
        Vecteur vecteur = image.toVecteur();
        Vecteur vecteur2 = image2.toVecteur();
        System.out.println(image.toString());
        vecteur.produitScalaire(vecteur2);
        System.out.print(vecteur.toString());

    }
}