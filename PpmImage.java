import java.awt.Color;
import java.io.*;
import java.util.Scanner;

/**
 * Class is an image in the ppm format
 */
class PpmImage extends Image {

    /**
     * Constructs the ppm with certain dimensions and colors it.
     * @param width the width of new image in pixels
     * @param height the height of the new image in pixels
     */
    public PpmImage(int width, int height) {
        super(width, height);
        blackFill();
    }
    public PpmImage(String filename){
        super();
        readImg(filename);
    }

    private void blackFill(){
        Color[][] cords = getColors();
        for(int i = 0; i < getHeight(); i++){
            for(int j = 0; j < getWidth(); j++){
                cords[i][j] = new Color(0, 0, 0);
            }
        }
    }

    private void readImg(String filename){
        try{
            Scanner given = new Scanner(new File(filename));
            given.nextLine();
            int width = given.nextInt();
            int height = given.nextInt();
            setWidth(width);
            setHeight(height);
            setColors(new Color[height][width]);

            int max = given.nextInt();
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int r = given.nextInt();
                    int g = given.nextInt();
                    int b = given.nextInt();
                    getColors()[i][j] = new Color(r, g, b);
                }
            }
            given.close();
        }catch(FileNotFoundException e){
            System.err.println("File not found: " + filename);
        }
    }

    @Override
    public void output(String filename){
        try(PrintWriter out = new PrintWriter(new FileWriter(filename))){
            out.println("P3");
            out.println(getWidth() + " " + getHeight());
            out.println(255);

            Color[][] cords = getColors();
            for(int i = 0; i < getHeight(); i++){
                for(int j = 0; j < getWidth(); j++){
                    Color pixel = cords[i][j];
                    out.println(pixel.getRed() + " " + pixel.getGreen() + " " + pixel.getBlue());
                }
            }
        }catch(IOException e){
            System.err.println("Error writing to file " + filename);
        }
    }
}
