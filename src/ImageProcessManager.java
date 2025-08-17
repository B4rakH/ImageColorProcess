import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageProcessManager {

    //Input image path
    private static final String ORIGINAL_PATH = "./resources";

    //output image path and name
    private static final String UPLOAD_PATH = "./output/output.jpg";

    private static long duration = 0;

    public static void startRecolor(int threadCount) throws IOException{

        File image = getImage();

        if (image == null) {
            System.out.println("File in the resources doesn't exist");
            return;
        }

        BufferedImage originalImg = ImageIO.read(getImage());
        BufferedImage uploadedImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();

        recolorMultiThread(originalImg, uploadedImg, threadCount);

        long endTime = System.currentTimeMillis();

        File outputFile = new File(UPLOAD_PATH);

        ImageIO.write(uploadedImg, "jpg", outputFile);

        duration = endTime-startTime;
    }

    private static void recolorSingleThread(BufferedImage original, BufferedImage newImg) {
        recolorImage(original, newImg, 0, 0, original.getHeight(), original.getWidth());
    }

    private static void recolorMultiThread(BufferedImage original, BufferedImage newImg, int threadCount) {

        List<Thread> threadList = new ArrayList<>();

        int sub_height = original.getHeight() / threadCount;

        for (int i = 0; i < threadCount; i++) {

            final int topCorner = sub_height * i;

            threadList.add(new Thread(() -> {
                recolorImage(original, newImg, 0, topCorner, sub_height, original.getWidth());
            }));
        }

        for (Thread thread : threadList){ thread.start(); }

        for(Thread thread : threadList)

            try {
                thread.join();
            } catch (InterruptedException e)
            {
                System.out.println(e.getMessage());
            }
    }

    private static void recolorImage(BufferedImage original, BufferedImage newImg, int leftCorner, int topCorner,
                                    int height, int width) {
        for (int x = leftCorner ; x < leftCorner + width && x < original.getWidth() ; x++) {
            for (int y = topCorner; y < topCorner + height && y < original.getHeight(); y++) {
                recolorPixel(original, newImg, x, y);
            }
        }
    }

    private static boolean isWhiteLikePixel(int r, int g, int b) {
        return Math.abs(r - b) < 30 && Math.abs(g - b) < 30 && Math.abs(r - g) < 30;
    }

    private static void recolorPixel(BufferedImage originalImg, BufferedImage newImg, int x, int y) {
        int rgb = originalImg.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if (isWhiteLikePixel(red, green, blue)) {

            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);

        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRgb = createRgb(newRed, newGreen, newBlue);
        setRgb(newImg, x, y, newRgb);
    }

    //Get red values of the ARGB value
    private static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    //Get green values of the ARGB value
    private static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    //Get blue values of the ARGB value
    private static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

    //Create rgb using red, green, blue and alpha values
    private static int createRgb(int r, int g, int b) {
        int rgb = 0;

        rgb |= b;
        rgb |= g << 8;
        rgb |= r << 16;

        return rgb;
    }

    private static void setRgb(BufferedImage newImage, int x, int y, int rgb) {
        newImage.getRaster().setDataElements(x, y, newImage.getColorModel().getDataElements(rgb, null));
    }

    private static File getImage(){
        File folder = new File(ORIGINAL_PATH);
        for (File image : folder.listFiles())
            if (image.isFile())  return image;

        return null;
    }

    public static long getDuration() { return duration; }
}
