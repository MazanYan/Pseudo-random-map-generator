package planet_map;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * on a complete resource map each cell can contain multiple resources
 */
public class ResourcesGenerator {
    private Hashtable<String,Double[]> resource_types;
    private Hashtable<String,RandomNoise> resource_maps;
    private static final int height = 160;
    private static final int width = 320;
    private static final int detalisation = 1;
    private static final int pix_height = height*2+1;
    private static final int pix_width = width*2+1;
    private int seed;
    private ArrayList<String>[][] map;

    /**
     * resource_types - hash table with resource name, it's occurance and +value for seed while resource map generating
     * (to make resource maps looking different)
     * @param seed - seed for maps generating
     */
    public ResourcesGenerator(int seed) {
        this.seed = seed;
        resource_types = new Hashtable<>();
        resource_types.put("CL", new Double[]{1.8,0.0});
        resource_types.put("OI",new Double[]{1.83,1.0});
        resource_types.put("GS",new Double[]{1.83,2.0});
        resource_types.put("IR",new Double[]{1.87,3.0});
        resource_types.put("CO",new Double[]{1.95,4.0});
        resource_types.put("AU",new Double[]{1.99,5.0});
        this.map = generate_resource_map();
    }

    private RandomNoise noise_generate(int seed, double occurance) {
        return new RandomNoise(height,width,detalisation, seed).make_multioctaved(1).minus(occurance).minus(-1).divide(0.001);
    }

    /**
     * Generates map of all resources
     * coal - 1.8
     * oil - 1.83
     * gas - 1.83
     * iron - 1.87
     * copper - 1.95
     * gold - 1.99
     * @return - two-domensional array of ArrayLists: each ArrayList contains resources that each cell contains
     */
    private ArrayList<String>[][] generate_resource_map() {
        ArrayList<String>[][] result = new ArrayList[pix_height][pix_width];
        for (int i = 0; i < pix_height; i++) {
            for (int j = 0; j < pix_width; j++) {
                result[i][j] = new ArrayList<>();
            }
        }
        resource_maps = new Hashtable<>();
        resource_types.forEach((type,occurance)->{
            resource_maps.put(type,noise_generate(seed+occurance[1].intValue(),occurance[0]));
        });
        resource_maps.forEach((type,map) -> {
            for (int i = 0; i < pix_height; i++) {
                for (int j = 0; j < pix_width; j++) {
                    if (map.get(i,j) >= 1)
                        result[i][j].add(type);
                }
            }
        });
        return result;
    }

    /**
     * mrthod to return our map, each map cell can contain multiple resource types
     * @return - map of resources
     */
    public ArrayList<String>[][] resource_map() {
        return map;
    }

    public String[][][] toArray() {
        int resources_quantity = resource_types.size();
        String[][][] result = new String[pix_height][pix_width][resources_quantity];
        for (int i = 0; i < pix_height; i++) {
            for (int j = 0; j < pix_width; j++) {
                int len = map[i][j].size();
                for (int k = 0; k < len; k++) {
                    result[i][j][k] = map[i][j].get(k);
                }
                for (int k = len; k < resources_quantity; k++) {
                    result[i][j][k] = "  ";
                }
            }
        }
        return result;
    }
}
