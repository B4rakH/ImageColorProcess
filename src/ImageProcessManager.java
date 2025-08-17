import java.awt.image.BufferedImage;

public class ImageProcessManager {

    //Input image path
    public static final String ORIGINAL_PATH = "./resource/a.jpg";

    //output image path and name
    public static final String UPLOAD_PATH = "./output/output.jpg";

    //Get red values of the ARGB value
    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    //Get green values of the ARGB value
    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    //Get blue values of the ARGB value
    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

    //TODO: will be tested
    public static int getAlpha(int rgb) {
        return (rgb & 0xFF000000) >> 24;
    }

    //Create rgb using red, green, blue and alpha values
    public static int createRgb(int r, int g, int b, int a) {
        int rgb = 0;

        rgb |= a << 24;
        rgb |= b;
        rgb |= g << 8;
        rgb |= r << 16;

        return rgb;
    }
}
