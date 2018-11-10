package planet_map;

import java.util.Random;

/**

=================================================Heightmap definition===================================================

*/
public class HeightmapNoise extends RandomNoise {
    public HeightmapNoise(int height, int width, int detalisation, int seed) {
        super(height, width, detalisation, seed);
        make_multioctaved();
        //divide(1.5);
    }

    public int[][] toHeightmap(double water_percent, int max_elevation) {
        RandomNoise height_noise = new HeightmapNoise(height, width, step, seed + 1);
        height_noise.make_multioctaved(4);
        height_noise.divide(1.1);
        double[][] noise = height_noise.toArray();
        int pix_height = height * step + 1;
        int pix_width = width * step + 1;
        double min_as_sea = water_percent * 2 - 1;
        int[][] res = new int[pix_height][pix_width];
        for (int i = 0; i < pix_height; i++) {
            for (int j = 0; j < pix_width; j++) {
                double noise_value = points_array[i][j].getColor();
                if (noise_value > min_as_sea)
                    res[i][j] = (int) Math.ceil(max_elevation * (noise[i][j] + 1) / 2);
                else
                    res[i][j] = 0;
            }
        }

        int[][] sea_distances = (new SeaDistance(height * step + 1, width * step + 1, res)).define_sea_distances();
        Random r = new Random(seed);
        for (int i = 0; i < sea_distances.length; i++) {
            int sea_distance_to_decrease = 10;
            for (int j = 0; j < sea_distances[0].length; j++) {
                if (res[i][j] > 0 && sea_distances[i][j] <= sea_distance_to_decrease) {
                    int new_height = (int)Math.floor((res[i][j] * (double) height_interpolation(sea_distances[i][j]) / sea_distance_to_decrease));
                    res[i][j] = new_height;
                }
            }
        }
        return res;
    }

    private int height_interpolation(int x) {
        return 2*x-x*x/10;
    }
}