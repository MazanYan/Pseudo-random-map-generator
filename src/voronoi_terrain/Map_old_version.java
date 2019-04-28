package voronoi_terrain;

import voronoi_terrain.Terrain;

import java.util.Random;

public class Map_old_version {
    private Terrain terrain_types;
    private int[][] heightmap;
    private int seed;
    private static final int height = 100;
    private static final int width = 100;
    private static final int detalisation = 1;
    private static final int base_divisions_num = 2500;

    public Map_old_version(double water_percent, int seed) {
        this.seed = seed;
        terrain_types = new Terrain(height,width,detalisation,base_divisions_num,water_percent,seed);
        heightmap = new int[terrain_types.height()][terrain_types.width()];
        for (int i = 0; i < terrain_types.height(); i ++) {
            for (int j = 0; j < terrain_types.width(); j++) {
                heightmap[i][j] = terrain_types.isLand(i,j) ? 1 : 0;
            }
        }
        change_height(heightmap,new int[]{50,50},100);
    }

    /**
     * A function that makes a random vector with predefined phi (from 0 to 2*pi) and length 1 stroll in direction of random line
     * and increase height of the passed cells
     *
     * delta_phi - a random deviation of the height-changing vector during each step
     * @param terr
     * @param steps_num
     * @return
     */
    private int[][] change_height(int[][] terr, int[] start_point, int steps_num) {
        Random rand = new Random(seed);
        double delta_phi;
        int module = 20;
        double angle = rand.nextDouble()*Math.PI*2;
        int[] vector = new int[]{-(int)Math.floor(module * Math.cos(angle)), -(int)Math.floor(module * Math.sin(angle))};
        while (steps_num > 0) {
            int start_x = start_point[0];
            int start_y = start_point[1];
            int end_x = start_x + vector[0];
            int end_y = start_y + vector[1];
            if (terr[end_x][end_y] > 0)
                terr[end_x][end_y] += 1;
            delta_phi = rand.nextDouble()*2*Math.PI-Math.PI;
            angle+=delta_phi;
            System.out.println(angle + " [" + vector[0] + ", " + vector[1] +"] " + "[" + end_x + ", " +end_y + "]");
            vector = new int[]{(int)Math.floor(module * Math.cos(angle)), (int)Math.floor(module * Math.sin(angle))};

            steps_num--;
        }

        return terr;
    }

    public int[][] getHeightmap() {
        return heightmap;
    }
}
