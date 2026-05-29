//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


public class Main {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/archive/test2.pgm";
        String path2 = System.getProperty("user.dir") + "/archive/test.pgm";
        Image image = new Image("image_test");
        image.chargerImagePGM(path);
        Image image2 = new Image("Image_test2");
        image2.chargerImagePGM(path2);
        Vecteur[] vecteurs = new Vecteur[2];
        Vecteur vecteur = image.toVecteur();
        Vecteur vecteur2 = image2.toVecteur();  
        System.out.println(image.toString());

        Image imagePPM = new Image("imagePPM");
        imagePPM.chargerImagePPM("archive/imagePPM/poivron_p3.ppm");
        imagePPM.creerFichierPGM("archive/imagePPM/poivron.pgm");

    }
}