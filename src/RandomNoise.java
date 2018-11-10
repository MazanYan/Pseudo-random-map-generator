package planet_map;

import java.util.Random;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class RandomNoise {
    protected int height;
    protected int width;
    protected int step;
    protected int seed;
    protected Point[][] points_array;
    private Random rand;

    /*
     * size - quantity of base points with randomly generated color values
     * step - quantity of transitional points between each base point
     */
    public RandomNoise(int height, int width, int detalisation, int seed) throws IllegalArgumentException {
        if (height < 1 || width < 1 || detalisation < 1)
            throw new IllegalArgumentException("Height, width and datalisation arguments must be positive integers");
        this.height = height;
        this.width = width;
        step = detalisation+1;
        this.seed = seed;
        points_array = new Point[this.height*step+1][this.width*step+1];
        rand = new Random(this.seed);
        /*
         * generating of array with base_points and transitional points between them
         * first coordinate - x (horisontal)
         * the second coordinate - y (vertical)
         */
        int x_coord = 0;
        int y_coord;
        for (int i = 0; i < points_array.length; i++) {
            y_coord = 0;
            for (int j = 0; j < points_array[0].length; j++) {
                if (x_coord % step == 0 && y_coord % step == 0) {
                    points_array[i][j] = new Point(x_coord, y_coord, rand.nextDouble()*2-1);
                }
                else
                    points_array[i][j] = new Point(x_coord, y_coord, 0);
                y_coord+= 1;
            }
            x_coord += 1;
        }
        apply_interpolation();
    }


    private void apply_interpolation() {
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                points_array[i][j] = apply_interpolation_to_point(points_array[i][j]);
            }
        }
    }

    public int getPixelsWidth() {
        return width*step+1;
    }

    public int getPixelsHeight() {
        return height*step+1;
    }

    public int getDetalisation() {
        return step;
    }

    public RandomNoise minus(double num) {
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                double new_color = points_array[i][j].getColor() - num;
                points_array[i][j].setColor(new_color);
            }
        }
        support_color();
        return this;
    }

    /**
     * Makes a noise to be suitable for map generating
     * "flatness" of changed noise map depends strongly on how much are width and height of matrix powers of 2
     * the best variant is when width or height are divisable on 2^5 and larger powers of 2
     */
    public RandomNoise make_multioctaved(int steps) {
        /*
         * Calculate number of necessary iterations
         */
        int iterations_num = -1;
        float test_1 = width;
        float test_2 = height;
        while (test_1 == Math.floor(test_1) && test_2 == Math.floor(test_2)) {
            test_1/=2.0;
            test_2/=2.0;
            iterations_num++;
        }

        iterations_num = iterations_num > steps ? steps : iterations_num;
        /*
         * Pre-division of basic matrix
         */
        int operand_height = height;
        int operand_width = width;
        int operand_detalisation = step;
        int powerer = (int)Math.pow(2,iterations_num);
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                double new_color = points_array[i][j].getColor()/((int)Math.pow(2, iterations_num));
                points_array[i][j].setColor(new_color);
            }
        }

        /*
         * With each iteration a matrix with lower amount of base points and higher detalisation is added,
         * predivided on 2**iteration before
         */
        while (iterations_num > 0) {
            powerer/=2;
            operand_height/=2;
            operand_width/=2;
            operand_detalisation = operand_detalisation*2 + 1;
            RandomNoise operand = new RandomNoise(operand_height, operand_width,operand_detalisation, seed);
            operand.divide(powerer);
            double[][] operand_colors = operand.toArray();
            for (int i = 0; i < points_array.length; i++) {
                for (int j = 0; j < points_array[0].length; j++) {
                    double new_color = points_array[i][j].getColor()+operand_colors[i][j];
                    points_array[i][j].setColor(new_color);
                }
            }
            support_color();
            iterations_num--;
        }
        return this;
    }

    public RandomNoise make_multioctaved() {
        /*
         * Calculate number of necessary iterations
         */
        int iterations_num = -1;
        float test_1 = width;
        float test_2 = height;
        while (test_1 == Math.floor(test_1) && test_2 == Math.floor(test_2)) {
            test_1/=2.0;
            test_2/=2.0;
            iterations_num++;
        }
        return make_multioctaved(iterations_num);
//        /*
//         * Pre-division of basic matrix
//         */
//        int operand_height = height;
//        int operand_width = width;
//        int operand_detalisation = step;
//        int powerer = (int)Math.pow(2,iterations_num);
//        for (int i = 0; i < points_array.length; i++) {
//            for (int j = 0; j < points_array[0].length; j++) {
//                double new_color = points_array[i][j].getColor()/((int)Math.pow(2, iterations_num));
//                points_array[i][j].setColor(new_color);
//            }
//        }
//
//        /*
//         * With each iteration a matrix with lower amount of base points and higher detalisation is added,
//         * predivided on 2**iteration before
//         */
//        while (iterations_num > 0) {
//            powerer/=2;
//            operand_height/=2;
//            operand_width/=2;
//            operand_detalisation = operand_detalisation*2 + 1;
//            RandomNoise operand = new RandomNoise(operand_height, operand_width,operand_detalisation, seed);
//            operand.divide(powerer);
//            double[][] operand_colors = operand.toArray();
//            for (int i = 0; i < points_array.length; i++) {
//                for (int j = 0; j < points_array[0].length; j++) {
//                    double new_color = points_array[i][j].getColor()+operand_colors[i][j];
//                    points_array[i][j].setColor(new_color);
//                }
//            }
//            support_color();
//            iterations_num--;
//        }
//        return this;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < points_array.length; i++) {
            res.append("\n");
            for (int j = 0; j < points_array[0].length; j++) {
                res.append(points_array[i][j].toString());
            }
        }
        return res.toString();
    }

    private void support_color() {
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                if (points_array[i][j].getColor()>=1)
                    points_array[i][j].setColor(1);
                if (points_array[i][j].getColor()<=-1)
                    points_array[i][j].setColor(-1);
            }
        }
    }
    public RandomNoise plus(RandomNoise matrix_2) throws IllegalArgumentException{
        if (matrix_2.getPixelsWidth() != width*step+1 || matrix_2.getPixelsHeight() != height*step+1)
            throw new IllegalArgumentException("Noise matrixes must have the same size to be added");
        double[][] matrix_2_colors = matrix_2.toArray();
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                double new_color = points_array[i][j].getColor()+matrix_2_colors[i][j];
                points_array[i][j].setColor(new_color);
            }
        }
        support_color();
        return this;
    }

    public RandomNoise divide(double divider) throws ArithmeticException {
        if (divider == 0)
            throw new ArithmeticException("/ by zero");
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                double new_color = points_array[i][j].getColor()/divider;
                if (new_color > 1)
                    points_array[i][j].setColor(1);
                if (new_color < -1)
                    points_array[i][j].setColor(-1);
                else
                    points_array[i][j].setColor(new_color);
            }
        }
        support_color();
        return this;
    }

    /**
     * Interpolate non-anchor point using cubic interpolation
     * @param pt - point on which the interpolation is going to be applied
     * @return - interpolated point
     */
    private Point apply_interpolation_to_point(Point pt) {
        int[] coordinates = pt.getCoordinates();
        int pt_x = coordinates[0];
        int pt_y = coordinates[1];

        /*
         * Don't change base_points color values
         */
        if (pt_x % step == 0 && pt_y % step == 0)
            return pt;

        /*
         * Point not in row and column with base points
         */
        if (pt_x % step != 0 && pt_y % step !=0){
            /*
             * Color values and coordinates of non-base points' square edges
             */

            Point top_l = points_array[pt_x - pt_x%step][pt_y-pt_y%step];
            Point top_r = points_array[pt_x + (step - pt_x%step)][pt_y-pt_y%step];
            Point bot_l = points_array[pt_x - pt_x%step][pt_y + (step - pt_y%step)];
            Point bot_r = points_array[pt_x + (step - pt_x%step)][pt_y + (step - pt_y%step)];

            int[] top_l_coords = top_l.getCoordinates();

            double top_l_color = top_l.getColor();
            double top_r_color = top_r.getColor();
            double bot_l_color = bot_l.getColor();
            double bot_r_color = bot_r.getColor();

            /*
             * Calculate coordinates of our point inside it's square and then interpolate it
             * to make a transition between base points more steady
             */
            int in_square_coords_x = pt_x-top_l_coords[0];
            int in_square_coords_y = pt_y-top_l_coords[1];
            float interpolated_in_square_coords_x = cubic_coordinates_interpolation(in_square_coords_x);
            float interpolated_in_square_coords_y = cubic_coordinates_interpolation(in_square_coords_y);
            double top_interpolated = linear_interpolation(top_l_color, top_r_color, interpolated_in_square_coords_x*step);
            double bot_interpolated = linear_interpolation(bot_l_color, bot_r_color, interpolated_in_square_coords_x*step);
            double completely_interpolated = linear_interpolation(top_interpolated, bot_interpolated, interpolated_in_square_coords_y*step);

            return new Point(coordinates[0], coordinates[1], completely_interpolated);
        }

        /*
         * Point in row with base points
         */
        if (pt_x % step != 0 && pt_y % step == 0) {
            Point left = points_array[pt_x - pt_x%step][pt_y];
            Point right = points_array[pt_x + (step - pt_x%step)][pt_y];

            int[] left_coords = left.getCoordinates();

            double left_color = left.getColor();
            double right_color = right.getColor();

            int in_line_coords = pt_x-left_coords[0];

            float interpolated_in_square_coords = cubic_coordinates_interpolation(in_line_coords);
            double interpolated = linear_interpolation(left_color, right_color, interpolated_in_square_coords*step);

            return new Point(coordinates[0], coordinates[1], interpolated);

        }
        /*
         * Point in column with base points
         */
        if (pt_x % step == 0 && pt_y % step != 0) {
            Point top = points_array[pt_x][pt_y - pt_y%step];
            Point bot = points_array[pt_x][pt_y + (step - pt_y%step)];

            int[] top_coords = top.getCoordinates();

            double top_color = top.getColor();
            double bot_color = bot.getColor();

            int in_line_coords = pt_y-top_coords[1];

            float interpolated_in_square_coords = cubic_coordinates_interpolation(in_line_coords);
            double interpolated = linear_interpolation(top_color, bot_color, interpolated_in_square_coords*step);

            return new Point(coordinates[0], coordinates[1], interpolated);

        }
        else return pt;
    }

    private double linear_interpolation(double left_val, double right_val, float coord) {
        float coordinate = coord/step;
        return left_val*(1-coordinate) + right_val*coordinate;
    }
    private float cubic_coordinates_interpolation(int coord) {
        float new_coord = (float)coord/step;
        float new_new_coord = -2*new_coord*new_coord*new_coord+3*new_coord*new_coord;
        return new_new_coord;
    }

    public double[][] toArray() {
        double[][] return_val = new double[height*step+1][width*step+1];
        for (int i = 0; i < points_array.length; i++) {
            for (int j = 0; j < points_array[0].length; j++) {
                return_val[i][j] = round(points_array[i][j].getColor(),3);
            }
        }
        return return_val;
    }

    public double get(int i, int j) throws IllegalArgumentException {
        if (i < 0 || j < 0 || i >= height*step+1 || j >= width*step+1)
            throw new IllegalArgumentException("Access tono-existant elements of array");
        return points_array[i][j].color_value;
    }

    public void save_to_map(String filename, double water_percent) {
        BufferedImage image = new BufferedImage(width*step+1,height*step+1, BufferedImage.TYPE_INT_RGB);
        double color;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                color = (points_array[i][j].getColor()+1)/2;
                int red;
                int green;
                int blue;
                if (color >= water_percent) {
                    red = (int) (color * 255);
                    green = 128;
                    blue = 0;
                } else {
                    red = 0;
                    green = 0;
                    blue = 255;
                }
                int p = (red<<16) | (green<<8) | blue;
                image.setRGB(j,i,p);
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    public void save_to_png(String filename) {
        BufferedImage image = new BufferedImage(width*step+1,height*step+1, BufferedImage.TYPE_INT_RGB);
        double color;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                color = (points_array[i][j].getColor()+1)/2;
                int red = (int)(color*255);
                int green = (int)(color*255);
                int blue = (int)(color*255);
                int p = (red<<16) | (green<<8) | blue;
                image.setRGB(j,i,p);
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    protected class Point {
        private double color_value;
        private int[] coordinates;
        Point(int x, int y, double color_value) {
            this.color_value = color_value;
            this.coordinates = new int[]{x,y};
        }

        @Override
        public String toString() {
            return String.format("%-20s",round(color_value,3) + " [" + coordinates[0] + ", " + coordinates[1] + "]");
        }

        public int[] getCoordinates() {
            return coordinates;
        }

        public double getColor() {
            return color_value;
        }

        public boolean setColor(double color) {
            color_value = color;
            return true;
        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
