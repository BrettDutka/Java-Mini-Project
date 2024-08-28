import java.awt.*;
import java.io.FileNotFoundException;

/**
 * Processes certain operations for .ppm images.
 * Some of the things that are included in this are zero red, grayscale, inverse, cropping, mirroring, and repeating.
 */
class ImageOperations {

    /**
     * This is the main method that will process the command line arguments for the image operations.
     * @param args command line arguments which make the right image operation be performed.
     * @throws FileNotFoundException if the input file is not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        if(args.length < 2){
            System.out.println("Usage: java ImageOperations <operation> <inputFile> <outputFile> [additional arguments]");
            return;
        }

        String op = args[0];
        String input = args[args.length - 2];
        String outputF = args[args.length - 1];

        try {
            PpmImage img = new PpmImage(input);

            switch (op) {
                case "--zerored":
                    PpmImage zeroRedImg = (PpmImage) zeroRed(img);
                    zeroRedImg.output(outputF);
                    break;
                case "--grayscale":
                    PpmImage grayscaleImg = (PpmImage) grayscale(img);
                    grayscaleImg.output(outputF);
                    break;
                case "--invert":
                    PpmImage invertedImg = (PpmImage) invert(img);
                    invertedImg.output(outputF);
                    break;
                case "--crop":
                    if (args.length != 7) {
                        System.out.println("Usage for crop: --crop <x> <y> <width> <height> <inputFile> <outputFile>");
                        return;
                    }
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int width = Integer.parseInt(args[3]);
                    int height = Integer.parseInt(args[4]);
                    PpmImage cropped = (PpmImage) crop(img, x, y, width, height);
                    cropped.output(outputF);
                    break;
                case "--mirror":
                    if (args.length < 4) {
                        System.out.println("Usage for mirror: --mirror [H|V] <inputFile> <outputFile>");
                        return;
                    }
                    String mode = args[1];
                    PpmImage mirrored = (PpmImage) mirror(img, mode);
                    mirrored.output(outputF);
                    break;
                case "--repeat":
                    if (args.length < 5) {
                        System.out.println("Usage for repeat: --repeat [H|V] <n> <inputFile> <outputFile>");
                        return;
                    }
                    int n = Integer.parseInt(args[2]);
                    String dir = args[1];
                    PpmImage repeated = (PpmImage) repeat(img, n, dir);
                    repeated.output(outputF);
                    break;
                default:
                    System.out.println("File not found: " + input);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Removes any red from the ppm file.
     * @param img the given image that will be modified (pm.ppm)
     * @return the modified image with no red in it.
     */
    public static Image zeroRed(Image img){
        Color[][] colors = img.getColors();
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color oldColor = colors[i][j];
                colors[i][j] = new Color(0, oldColor.getGreen(), oldColor.getBlue());
            }
        }
        img.setColors(colors);
        return img;
    }

    /**
     * Changes the image to grayscale using average method.
     * @param img converts given image to grayscale (pm.ppm)
     * @return the grayscale image
     */
    public static Image grayscale(Image img){
        Color[][] colors = img.getColors();
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color old = colors[i][j];
                int scale = (old.getRed() + old.getGreen() + old.getBlue()) / 3;
                colors[i][j] = new Color(scale, scale, scale);

            }
        }
        img.setColors(colors);
        return img;

    }

    /**
     * Inverts the colors of the image
     * @param img the given image to convert (pm.ppm)
     * @return the inverted image
     */
    public static Image invert(Image img){
        Color[][] colors = img.getColors();
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j <img.getWidth(); j++){
                Color old = colors[i][j];
                colors[i][j] = new Color(255 - old.getRed(), 255 - old.getGreen(), 255 - old.getBlue());
            }
        }
        img.setColors(colors);
        return img;
    }

    /**
     * Crops the image to the specified dimensions and from the specific starting point given.
     * @param img the given image to crop (pm.ppm)
     * @param x the x-cord to crop
     * @param y the y-cord to crop
     * @param width the width of the crop area
     * @param height the height of the crop area
     * @return the cropped image
     */
    public static Image crop(Image img, int x, int y, int width, int height){
        Color[][] org = img.getColors();
        Color[][] newC = new Color[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                newC[i][j] = org[y + i][x + j];
            }
        }
        img.setHeight(height);
        img.setWidth(width);
        img.setColors(newC);
        return img;
    }

    /**
     * Mirrors the image horizontally or vertically depending on the command line argument.
     * @param img the given image to mirror (pm.ppm)
     * @param mode the mirroring mode given in the command line arguments either H or V for horizontal or vertical
     * @return the mirrored image
     */
    public static Image mirror(Image img, String mode){
        Color[][] colors = img.getColors();
        if("H".equalsIgnoreCase(mode)){
            for(int i = img.getHeight() / 2 + 1; i < img.getHeight(); i++){
                for(int j = 0; j < img.getWidth(); j++){
                    colors[i][j] = colors[img.getHeight() - 1 - i][j];
                }
            }
        }
        else if("V".equalsIgnoreCase(mode)){
            for(int i = 0; i < img.getHeight(); i++){
                for(int j = img.getWidth() / 2 + 1; j < img.getWidth(); j++){
                    colors[i][j] = colors[i][img.getWidth() - 1 - j];
                }
            }
        }
        img.setColors(colors);
        return img;
    }

    /**
     * Repeats the image horizontally or vertically depending on the command line arguments.
     * @param img the given image to repeat (pm.ppm)
     * @param n the number of times to repeat the image
     * @param dir the direction to repeat the image either H or V for horizontal or vertical.
     * @return the repeated image.
     */
    public static Image repeat(Image img, int n, String dir){
        int newWidth = dir.equalsIgnoreCase("H") ? img.getWidth() * n : img.getWidth();
        int newHeight = dir.equalsIgnoreCase("V") ? img.getHeight() * n : img.getHeight();
        Color[][] colors = img.getColors();
        Color[][] newC = new Color[newHeight][newWidth];
        for(int i = 0; i < newHeight; i++){
            for(int j = 0; j < newWidth; j++){
                newC[i][j] = colors[i % img.getHeight()][j % img.getWidth()];
            }
        }
        img.setHeight(newHeight);
        img.setWidth(newWidth);
        img.setColors(newC);
        return img;


    }
}

