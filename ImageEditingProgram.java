
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageEditingProgram {

    private static final String PNG_FORMAT = "png";
    private static final String NON_RGB_WARNING = "Warning: we do not support the image you provided. \n" +
            "Please change another image and try again.";
    private static final String RGB_TEMPLATE = "(%3d, %3d, %3d) ";
    private static final int BLUE_BYTE_SHIFT = 0;
    private static final int GREEN_BYTE_SHIFT = 8;
    private static final int RED_BYTE_SHIFT = 16;
    private static final int ALPHA_BYTE_SHIFT = 24;
    private static final int BLUE_BYTE_MASK = 0xff << BLUE_BYTE_SHIFT;
    private static final int GREEN_BYTE_MASK = 0xff << GREEN_BYTE_SHIFT;
    private static final int RED_BYTE_MASK = 0xff << RED_BYTE_SHIFT;
    private static final int ALPHA_BYTE_MASK = ~(0xff << ALPHA_BYTE_SHIFT);

   
    static int[][] image;

    /**
     * Open an image from disk and return a 2D array of its pixels.
     * Use 'load' if you need to load the image into 'image' 2D array instead
     * of returning the array.
     *
     * @param pathname path and name to the file, e.g. "input.png",
     *                 "D:\\Folder\\ucsd.png" (for Windows), or
     *                 "/User/username/Desktop/my_photo.png" (for Linux/macOS).
     *                 Do NOT use "~/Desktop/xxx.png" (not supported in Java).
     * @return 2D array storing the rgb value of each pixel in the image
     * @throws IOException when file cannot be found or read
     */
    public static int[][] open(String pathname) throws IOException {
        BufferedImage data = ImageIO.read(new File(pathname));
        if (data.getType() != BufferedImage.TYPE_3BYTE_BGR &&
                data.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            System.err.println(NON_RGB_WARNING);
        }
        int[][] array = new int[data.getHeight()][data.getWidth()];

        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                /*
                 * Images are stored by column major
                 * i.e. (2, 10) is the pixel on the column 2 and row 10
                 * However, in class, arrays are in row major
                 * i.e. [2][10] is the 11th element on the 2nd row
                 * So we reverse the order of i and j when we load the image.
                 */
                array[row][column] = data.getRGB(column, row) & ALPHA_BYTE_MASK;
            }
        }

        return array;
    }

    /**
     * Load an image from disk to the 'image' 2D array.
     *
     * @param pathname path and name to the file, see open for examples.
     * @throws IOException when file cannot be found or read
     */
    public static void load(String pathname) throws IOException {
        image = open(pathname);
    }

    /**
     * Save the 2D image array to a PNG file on the disk.
     *
     * @param pathname path and name for the file. Should be different from
     *                 the input file. See load for examples.
     * @throws IOException when file cannot be found or written
     */
    public static void save(String pathname) throws IOException {
        BufferedImage data = new BufferedImage(
                image[0].length, image.length, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                // reverse it back when we write the image
                data.setRGB(column, row, image[row][column]);
            }
        }
        ImageIO.write(data, PNG_FORMAT, new File(pathname));
    }

    /**
     * Unpack red byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return red value in that packed pixel, 0 <= red <= 255
     */
    private static int unpackRedByte(int rgb) {
        return (rgb & RED_BYTE_MASK) >> RED_BYTE_SHIFT;
    }

    /**
     * Unpack green byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return green value in that packed pixel, 0 <= green <= 255
     */
    private static int unpackGreenByte(int rgb) {
        return (rgb & GREEN_BYTE_MASK) >> GREEN_BYTE_SHIFT;
    }

    /**
     * Unpack blue byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return blue value in that packed pixel, 0 <= blue <= 255
     */
    private static int unpackBlueByte(int rgb) {
        return (rgb & BLUE_BYTE_MASK) >> BLUE_BYTE_SHIFT;
    }

    /**
     * Pack RGB bytes back to an int in the format of
     * [byte0: unused][byte1: red][byte2: green][byte3: blue]
     *
     * @param red   red byte, must satisfy 0 <= red <= 255
     * @param green green byte, must satisfy 0 <= green <= 255
     * @param blue  blue byte, must satisfy 0 <= blue <= 255
     * @return packed int to represent a pixel
     */
    private static int packInt(int red, int green, int blue) {
        return (red << RED_BYTE_SHIFT)
                + (green << GREEN_BYTE_SHIFT)
                + (blue << BLUE_BYTE_SHIFT);
    }

    /**
     * Print the current image 2D array in (red, green, blue) format.
     * Each line represents a row in the image.
     */
    public static void printImage() {
        for (int[] ints : image) {
            for (int pixel : ints) {
                System.out.printf(
                        RGB_TEMPLATE,
                        unpackRedByte(pixel),
                        unpackGreenByte(pixel),
                        unpackBlueByte(pixel));
            }
            System.out.println();
        }
    }

    /**
     * For all methods below, leave the original image unchanged
     * if the input values are invalid. If inputs are valid, then the
     * resulting image should be stored in the static varaible image.
     */

    /**
     * Rotate the image by degree degrees clockwise.
     * The input value is invalid if degree is less than 0 or
     * if degree is not evenly divisible by 90.
     * 
     * 
     * @param degree
     */
    public static void rotate(int degree) {
        if (degree < 0 || degree % 90 != 0) {
            return; // invalid input dont change image
        }

        else {
            // times to rotate
            for (int times = 0; times < degree / 90; times++) {
                int[][] pix = new int[image[0].length][image.length]; // switch each iteration

                for (int i = 0; i < image.length; i++) {
                    for (int j = 0; j < image[i].length; j++) {
                        pix[j][i] = image[image.length - 1 - i][j];
                    }
                }
                image = pix;
            }

        }

    }

    /**
     * heightScale: down-scaling factor for the height of the image
     * widthScale: down-scaling factor for the width of the image
     * 
     * Input values are invalid if heightScale or widthScale is
     * less than 1 or greater than the height or width respectively
     * NOT a multiplication factor of the height or width respectively
     * For example, if there are 8 rows (height is 8)
     * and heightScale is 3, 8 is not evenly divisible by 3 so the value of
     * heightScale is invalid.
     * The original image should be unchanged.
     */
    public static void downSample(int heightScale, int widthScale) {
        // height is how many rows. width is how many columns
        //fix conditions || heightScale % image.length != 0  || widthScale % image[0].length != 0
        if (heightScale < 1 || heightScale > image.length ) {
            return;
        }

        if (widthScale < 1 || widthScale > image[0].length ) {
            return;
        }

        if( image.length % heightScale  != 0 || image[0].length % widthScale != 0)
        {
            return;
        }

        int[][] downScaled = image;
        image = new int[ image.length / heightScale][image[0].length / widthScale ];
        // make a slider. iteriate thru slider to get rgb
        for (int a = 0; a < image.length; a++) // moving the grid
        {
            for (int b = 0; b < image[a].length; b++) {
                int avgred = 0;
                int avgblue = 0;
                int avggreen = 0;
                for (int c = 0; c < heightScale; c++) // sum rgb in grid
                {
                    for (int d = 0; d < widthScale; d++) {
                        avgred += unpackRedByte(downScaled[a * heightScale + c][b * widthScale + d]); // computer saw it
                                                                                                      // in binary
                        avgblue += unpackBlueByte(downScaled[a * heightScale + c][b * widthScale + d]);

                        avggreen += unpackGreenByte(downScaled[a * heightScale + c][b * widthScale + d]);

                    }
                }
                avgred /= (heightScale * widthScale);
                avgblue /= (heightScale * widthScale);
                avggreen /= (heightScale * widthScale);
                image[a][b] = packInt(avgred, avggreen, avgblue);
            }
        }
    }

    /**
     * patchImage: a 2D array representing RGB values of the patch image
     * transparentRed, transparentGreen, transparentBlue:
     * an integer representing the corresponding red, green, and blue component
     * for the transparent RGB color
     * 
     * Replace a certain part of image with patchImage, starting from position
     * [startRow][startColumn].
     * If the pixel's RGB value from patchImage matches the
     * transparent RGB color, we do not replace the pixel from image with the pixel
     * from patchImage.
     * Returns the number of pixels that we have patched.
     * 
     * @param startRow
     * @param startColumn
     * @param patchImage
     * @param transparentRed
     * @param transparentGreen
     * @param transparentBlue
     * @return
     */
    public static int patch(int startRow, int startColumn,
        int[][] patchImage, int transparentRed, int transparentGreen, int transparentBlue) {
        int patch = 0;
        //patch orignal image w new image
        if (startRow < 0 || startRow > image.length) {
            return 0;
        }
        if (startColumn < 0 || startColumn > image[0].length) {
            return 0;
        } 

        if( patchImage.length > image.length || patchImage[0].length > image[0].length ) //check start rows in bounds
        {
            return 0;
        }

        if( (patchImage[0].length == image[0].length) && startColumn > 0 ) //check start rows
        {
            return 0;
        }

        if( startRow + patchImage.length > image.length || startColumn + patchImage[0].length > image[0].length )
        {
            return 0;
        }

            int trans = packInt(transparentRed, transparentGreen, transparentBlue);

            for (int a = 0; a < patchImage.length; a++) {
                for (int b = 0; b < patchImage[a].length; b++) {      
                    if ( patchImage[a][b] == trans ) 
                    {
                        continue;
                    }
                        patch++;
                        image[a + startRow][b + startColumn] = patchImage[a][b];            
                    
                }
            }
            return patch;
        }

        public static void main(String[] args) throws IOException {
            load("long.png");
            int[][] patchedImage = open("gray.png");
            int patchedPixels = patch(0, 1, patchedImage, 160, 150, 140);
            System.out.println(patchedPixels);
            save("GRAYTEST.png");
            //gray should not patch since will go outofbound, patch image hanging off orig image
        }
}