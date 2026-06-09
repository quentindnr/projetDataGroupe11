package systeme_reconnaissance;

import java.io.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PGMLoader {

    public static Image loadPGM(File file) throws IOException {

        try (InputStream is = new FileInputStream(file)) {

            // lire format (P2 ou P5)
            String format = readToken(is);

            if (!format.equals("P2") && !format.equals("P5")) {
                throw new IOException("Format non supporté: " + format);
            }

            // largeur
            int width = Integer.parseInt(readToken(is));

            // hauteur
            int height = Integer.parseInt(readToken(is));

            int maxVal = Integer.parseInt(readToken(is));

            WritableImage img = new WritableImage(width, height);
            PixelWriter pw = img.getPixelWriter();

            if (format.equals("P2")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int val = Integer.parseInt(readToken(is));

                        pw.setColor(x, y, Color.gray(val / (double) maxVal));
                    }
                }

            } else {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int val = is.read(); // P5 = bytes

                        pw.setColor(x, y, Color.gray(val / (double) maxVal));
                    }
                }
            }

            return img;
        }
    }

    /*
     * Lit un token ASCII (ignore espaces / retours ligne / commentaires)
     */
    private static String readToken(InputStream is) throws IOException {

        StringBuilder sb = new StringBuilder();
        int c;

        // skip espaces
        do {
            c = is.read();
            if (c == '#') {
                while (is.read() != '\n')
                    ;
            }
        } while (Character.isWhitespace(c));

        // lire token
        while (c != -1 && !Character.isWhitespace(c)) {
            sb.append((char) c);
            c = is.read();
        }

        return sb.toString();
    }
}