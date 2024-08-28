import java.awt.*;

/**
 * Defines the properties for the images like dimensions and color of them.
 */
abstract class Image implements Writable{
    private int width;
    public int getWidth(){
        return width;
    }
    public void setWidth(int width){
        this.width = width;
    }
    private int height;
    public int getHeight(){
        return height;
    }
    public void setHeight(int height){
        this.height = height;
    }
    private Color[][] colors;
    public Color[][] getColors(){
        return colors;
    }
    public void setColors(Color[][] colors){
        this.colors = colors;
    }

    /**
     * Creates a new image with certain dimensions.
     * @param width the width of the img in pixels
     * @param height the height of the img in pixels
     */
    public Image(int width, int height) {
       this.colors = new Color[this.height][this.width];
    }
    public Image(){
        this.height = 0;
        this.width = 0;
    }
}

