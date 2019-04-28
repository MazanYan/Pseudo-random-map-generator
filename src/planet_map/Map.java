package planet_map;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Generates map dependant only of random noise generator
 * heightmap is also dependand only from it
 */
public class Map {
    private static final int max_elevation = 20;
    private static final int height = 160;
    private static final int width = 320;
    private static final int detalisation = 1;
    private static final int pix_height = height*2+1;
    private static final int pix_width = width*2+1;
    private double water_percent;
    private int[][] heightmap;
    private int[][] distances_from_sea;
    private String[][] biome_map;
    private String[][] resource_map;
    private HeightmapNoise noise_map;
    private ClimateGenerator clim;

    public Map(double water_percent, int seed) {
        this.water_percent = water_percent;
        noise_map = new HeightmapNoise(height,width,detalisation, seed);
        this.heightmap = noise_map.toHeightmap(water_percent,max_elevation);
        clim = new ClimateGenerator(heightmap);
        this.distances_from_sea = (new SeaDistance(this)).define_sea_distances();
        this.biome_map = clim.generate_biomes();
        //this.resource_map = (new ResourcesGenerator(seed)).getResourcesMap();
    }

/**

========================================General public class methods====================================================

     * Method that saves a heightmap into a png file
     * @param filename - name of the file an image is saved in
     */
    public void save_heightmap(String filename) {
        BufferedImage image = new BufferedImage(pix_width, pix_height, BufferedImage.TYPE_INT_RGB);
        double height;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                height = (double)heightmap[i][j];
                int red;
                int green;
                int blue;
                if (height > 0) {
                    red = (int)(height/ max_elevation *100)+155;
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

    public void save_humidity(String filename) {
        BufferedImage image = new BufferedImage(pix_width, pix_height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                double humidity = clim.humidity(i,j);
                image.setRGB(j,i,(int)(humidity*255));
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    public void save_temperature(String filename) {
        BufferedImage image = new BufferedImage(pix_width, pix_height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                double temperature = clim.temperature(i,j);
                image.setRGB(j,i,(int)(temperature*255) << 16);
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    public void save_climate(String filename) {
        Hashtable<String, Integer[]> colors_table = new Hashtable<>();
        colors_table.put("AD", new Integer[]{255,255,255});
        colors_table.put("TU",new Integer[]{0,216,180});
        colors_table.put("TA",new Integer[]{0,94,47});
        colors_table.put("TF",new Integer[]{0,182,61});
        colors_table.put("ST",new Integer[]{68,255,0});
        colors_table.put("HD",new Integer[]{255,255,0});
        colors_table.put("SF",new Integer[]{97,171,0});
        colors_table.put("DS",new Integer[]{216,195,0});
        colors_table.put("SV",new Integer[]{167,201,0});
        colors_table.put("RF",new Integer[]{0,115,19});
        colors_table.put("WT",new Integer[]{0,0,255});


        BufferedImage image = new BufferedImage(pix_width, pix_height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                String biome = biome_map[i][j];
                int red     = colors_table.get(biome)[0];
                int green   = colors_table.get(biome)[1];
                int blue    = colors_table.get(biome)[2];
                int p = (red<<16) | (green<<8) | blue;
                image.setRGB(j,i,p);
            }
        }
        File f = new File(filename);
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {}
    }

    public int getHeight() {
        return noise_map.getPixelsHeight();
    }

    public int getWidth() {
        return noise_map.getPixelsWidth();
    }
    public int[][] toHeightmap() {
        return heightmap;
    }

    public int[][] seaDistancesMap() {
        return distances_from_sea;
    }

}
