package voronoi_terrain;

import planet_map.RandomNoise;

public class Terrain {
    private double water_percent;
    private int seed;
    private RandomNoise noise;
    private AdaptedVoronoiDiagram voronoi_diagr;
    private static final int land = 255 << 8;
    private static final int sea = 255;

    public Terrain(int height, int width, int detalisation,int base_divisions_num, double water_percent, int seed) {
        this.water_percent = water_percent;
        this.seed = seed;
        noise = new RandomNoise(height, width, detalisation, seed);
        noise.make_multioctaved(100);
        double[][] noise_arr = noise.toArray();
        voronoi_diagr = new AdaptedVoronoiDiagram(noise.getPixelsHeight(),noise.getPixelsWidth(),base_divisions_num,seed,noise_arr);

    }

    public void save_to_png(String filename) {
        voronoi_diagr.save_to_png(filename);
    }
    private class AdaptedVoronoiDiagram extends VoronoiDiagram {

        public AdaptedVoronoiDiagram(int height, int width, int points_num, int seed, double[][] noise_array){
            super(height, width, points_num, seed);
            for (VoronoiDiagram.Point i : base_points) {
                int[] base_coords = i.getCoordinates();
                if ((noise_array[base_coords[0]][base_coords[1]]+1)/2 >= water_percent) {
                    i.setColor(land);
                }
                else {
                    i.setColor(sea);
                }
            }
            generate();
        }
    }

    public int height() {
        return noise.getPixelsHeight();
    }

    public int width() {
        return noise.getPixelsWidth();
    }

    public boolean isLand(int x, int y) {
        return (voronoi_diagr.getColor(x,y) == land);
    }

    public int[][] toArray() {
        return voronoi_diagr.toArray();
    }
}
