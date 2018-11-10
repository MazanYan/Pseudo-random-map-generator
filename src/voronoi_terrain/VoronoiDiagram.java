package voronoi_terrain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class VoronoiDiagram {
    protected int height;
    protected int width;
    private int seed;
    private Point[][] canvas;
    protected List<Point> base_points;
    private Random rand;

    public VoronoiDiagram(int height, int width, int points_num, int seed) {
        this.seed = seed;
        rand = new Random(seed);
        this.height = height;
        this.width = width;
        canvas = new Point[this.height][this.width];
        base_points = new ArrayList<>();
        for (int i = 1; i <= points_num; i++)
            base_points.add(new Point(rand.nextInt(height), rand.nextInt(width), rand.nextInt(16777215)));
        generate();
    }

    protected void generate() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                canvas[i][j] = new Point(i,j,0);
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double base_distance = distance(canvas[0][0], canvas[height - 1][width - 1]);
                for (Point point:base_points) {
                    if (distance(canvas[i][j], point) < base_distance) {
                        canvas[i][j].setColor(point.getColor());
                        base_distance = distance(canvas[i][j], point);
                    }
                }
            }
        }
    }

    public int[][] toArray() {
        int[][] res = new int[height][width];
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[0].length; j++) {
                res[i][j] = canvas[i][j].getColor();
            }
        }
        return res;
    }

    public void save_to_png(String filename) {
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        int color;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                color = canvas[i][j].getColor();
                image.setRGB(j, i, color);
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < height; i++) {
            res.append("\n");
            for (int j = 0; j < width; j++) {
                res.append(canvas[i][j].toString());
            }
        }
        return res.toString();
    }

    public int getColor(int x, int y) {
        return canvas[x][y].getColor();
    }

    private double distance(Point start, Point end) {
        int[] start_coords = start.getCoordinates();
        int[] end_coords = end.getCoordinates();
        return Math.sqrt(Math.pow(start_coords[0]-end_coords[0],2)+Math.pow(start_coords[1]-end_coords[1],2));
    }


    protected class Point {
        private int[] coordinates;
        private int color;
        Point(int x, int y, int color_identifier) {
            color = color_identifier;
            coordinates = new int[]{x,y};
        }

        @Override
        public String toString() {
            return "" + color + " [" + coordinates[0] + ", " + coordinates[1] + "]";
        }

        public int getColor() {
            return color;
        }
        public void setColor(int color) {
            this.color = color;
        }
        public int[] getCoordinates() {
            return coordinates;
        }
    }
}
