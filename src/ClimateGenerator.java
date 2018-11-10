package planet_map;

/**

 ========================================Climate/biomes map definition===================================================

 humidity =     double range[0,1]
 temperature =  double range[0,10]                                        0 = -30 Cels, 5 = 0 Cels, 10 = 30 Cels
 elevation =    int range[1,20]
 biome types =  {arctic desert, tundra, taiga, temperate forest, steppe,half desert, subtropical forest, desert, savanna, rainforest, water}

 humidity latitude multiplier values in several latitudes:
                                                                          (0;3) equivalent to (0;90) latitude degrees
 humidity (latitude multiplier):
 h(l) = -0.0266667*Math.pow(l,5) + 0.286667*Math.pow(l,4) - 1.14667*Math.pow(l,3) + 2.02833*Math.pow(l,2) - 1.36167*l + 0.95;
 humidity (sea distance multiplier):                                      multipliers dependant of distance
 h(d) = e^ (-d^2 / 2000)
 total humidity (dependant of point on the map):
 h(p) = h(l)*h(d)                                                         (will define the best formula later)

 temperature values on several latitudes:
 {{0,9},{1,51/6},{2,5},{3,0}}                                             (0;3) equivalent to (0;90) latitude degrees
 temperature (latitude multiplier):
 t(l) = l^3/4 - (9 l^2)/4 + (3 l)/2 + 9
 temperature (elevation multiplier):
 t(h) = -0.000156642 h^3 + 0.00178571 h^2 - 0.0189223 h + 1.01729
 total temperature (dependant from point on the map):
 t(p) = t(l)*t(h)

 */

public class ClimateGenerator {
    private int height;
    private int width;
    private int[][] heightmap;
    private int[][] distances_from_sea;
    private String[][] biome_map;

    public ClimateGenerator(int[][] heightmap) {
        this.heightmap = heightmap;
        height = heightmap.length;
        width = heightmap[0].length;
        distances_from_sea = (new SeaDistance(height,width,heightmap)).define_sea_distances();
    }
    private String determine_biome(int i, int j, int distance_from_sea, int height) {
        double temperature = calculate_temperature(i,j,height);
        double humidity = calculate_humidity(i,j,distance_from_sea);
        String biome_type = "";
        //arctic desert
        if (temperature <= 1)
            biome_type = "AD";
        //tundra
        if ((temperature > 1 && temperature <= 2) ||
                (humidity <= 0.5&& temperature > 2 && temperature <= 5))
            biome_type = "TU";
        //taiga
        if (humidity > 0.5 && temperature > 2 && temperature <= 5)
            biome_type = "TA";
        //temperate forest
        if (humidity > 0.5 && temperature > 5 && temperature <= 40.0/6)
            biome_type = "TF";
        //steppe
        if ((humidity <= 0.5 && humidity > 0.3 && temperature > 5 && temperature <= 40.0/6) ||
                (humidity <= 0.7 && temperature > 40.0/6 && temperature <= 7.5))
            biome_type = "ST";
        //half desert
        if ((humidity > 0.2 && humidity <= 0.3 && temperature > 3) ||
                (humidity > 0.3 && temperature > 3 && temperature <= 2))
            biome_type = "HD";
        //subtropical forest
        if (humidity > 0.7 && temperature > 40.0/6 && temperature <= 8)
            biome_type = "SF";
        //desert
        if (humidity <= 0.2 && temperature > 3)
            biome_type = "DS";
        //savanna
        if (humidity > 0.3 && humidity <= 0.7 && temperature > 7.5)
            biome_type = "SV";
        //rainforest
        if (humidity > 0.7 && temperature > 8)
            biome_type = "RF";
        //water
        if (heightmap[i][j] == 0)
            biome_type = "WT";
        return biome_type;
    }

    private double calculate_latitude(int i, int j) {
        double equator = (double)(height-1)/2;
        double to_our_coords = 3.0/equator;
        return Math.abs((i-equator)*to_our_coords);
    }
    private double calculate_humidity(int i, int j, int distance_from_sea) {
        if (heightmap[i][j] == 0)
            return 1;
        double l = calculate_latitude(i,j);
        double humidity_latitude = -0.0266667*Math.pow(l,5) + 0.286667*Math.pow(l,4) - 1.14667*Math.pow(l,3) + 2.02833*Math.pow(l,2) - 1.36167*l + 1;
        double humidity_sea_distance = Math.pow(Math.E,-Math.pow(distance_from_sea,2)/2000);
        return humidity_latitude*humidity_sea_distance > 0 ? humidity_latitude*humidity_sea_distance : 0;
    }
    private double calculate_temperature(int i, int j, int elevation) {
        double l = calculate_latitude(i,j);
        double temperature_latitude = Math.pow(l,3)/4 - (9*Math.pow(l,2))/4 + 3*l/2 + 9;
        double teperature_elevation = -0.000156642*Math.pow(elevation,3) + 0.00178571*Math.pow(elevation,2) - 0.0189223*elevation + 1.01729;
        return temperature_latitude*teperature_elevation;
    }

    public double temperature(int i, int j) {
        return calculate_temperature(i,j, heightmap[i][j])/10;
    }

    public double humidity(int i, int j) {
        return calculate_humidity(i,j,distances_from_sea[i][j]);
    }

    public String[][] generate_biomes(){
        biome_map = new String[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double humidity = calculate_humidity(i,j,distances_from_sea[i][j]);
                double temperature = calculate_temperature(i,j,heightmap[i][j]);
                biome_map[i][j] = determine_biome(i,j,distances_from_sea[i][j],heightmap[i][j]);
            }
        }
        return biome_map;
    }
}
