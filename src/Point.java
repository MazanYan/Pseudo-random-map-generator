package planet_map;
import game_map.GameMap;
import java.util.ArrayList;

/* point contains terrain type (land/water)
 * height, temperature, humidity
 * biome type
 * existing biome types =  {arctic desert :          AD
 *                          tundra :                 TU
 *                          taiga :                  TA
 *                          temperate forest :       TF
 *                          steppe :                 ST
 *                          half desert :            HD
 *                          subtropical forest :     SF
 *                          desert :                 DS
 *                          savanna :                SV
 *                          rainforest :             RF
 *                          water :                  WT
 *                          }
 * river = {if exists, direction}
 * country belongs to
 * road = {type, width, direction}
 * resources = {type, quantity}
 * types of resources : {coal :                      CL
 *                       oil :                       OI
 *                       gas :                       GS
 *                       iron :                      IR
 *                       copper :                    CO
 *                       gold :                      AU
 *                       
 * }
 * city = {name, size}
 * other buildings = {port, fortification etc}
 * additional information - to be added
 */

/**
 * input height : int range [0,20] for land [1,20] -> elevation : int range [0,10 000] (for land)
 * input temperature : double range [0,10] -> temperature : double range [-30, 30] (average temperature Celsius)
 * input humidity : double range [0,1] -> precipitation : int range [0,8 000] (mm) (uses non-linear interpolation)
 * input resources : ArrayList of resources
 *
 * road types : {dirt road, stone paved road, railroad, asphalt road}
 * road width : int range [1,3]
 *
 * river width : int range [1,3]
 * city sizes : long range [0, +infinity] (number of residents)
 * resources quantity : int range [1,5]
 */

public class Point {
    //necessary point characteristics
    private boolean is_land;
    private int elevation;
    private double temperature;
    private double humidity;
    private String biome_type;                      //biome variable can be only one of these two-symbols codes above

    private boolean has_resource = false;
    private ArrayList<String> resources;

    private boolean has_river = false;
    private int river_direction;

    private boolean has_city = false;
    private String city_name;
    private int city_size;

    private String country_owner = "";

    private boolean has_road = false;
    private String road_type;
    private int road_width;
    private int road_direction;

    private boolean has_additional_building = false;
    private String additional_building;
    GameMap map;

    public Point(int height, double temp, double hum, String biome) {
        is_land = !biome.equals("WT");
        elevation = height;
        temperature = temp;
        humidity = hum;
        biome_type = biome;
    }

    public void addResources(ArrayList<String> res) {
        has_resource = true;
        resources = res;
    }
    public void delResources() {
        has_resource = false;
        resources = null;
    }

    /**
     * directions:
     *      west       = -1
     *      north-west = -2
     *      north      = -3
     *      north-east = -4
     *      east       = 1
     *      south-east = 2
     *      south      = 3
     *      south-west = 4
     * @param direction - the direction in which the river flows
     */
    public void addRiver(int direction) {
        has_river = true;
        river_direction = direction;
    }

    public void delRiver() {
        has_river = false;
        river_direction = 0;
    }

    public void addCity(String name, int size) {
        has_city = true;
        city_name = name;
        city_size = size;
    }

    public void delCity() {
        has_city = false;
        city_name = null;
        city_size = 0;
    }

    public void addCountry(String name) {
        country_owner = name;
    }

    public void delCountry() {
        country_owner = null;
    }

    public void addRoad(int width, int direction, String type) {
        has_road = true;
        road_width = width;
        road_direction = direction;
        road_type = type;
    }

    public void delRoad() {
        has_road = false;
        road_width = 0;
        road_direction = 0;
        road_type = null;
    }

    public void addAdditionalBuilding(String name) {
        has_additional_building = true;
        additional_building = name;
    }

    public void delAdditionalBuilding() {
        has_additional_building = false;
        additional_building = null;
    }
}
