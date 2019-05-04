import planet_map.Map;
import planet_map.RandomNoise;
import planet_map.ResourcesGenerator;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int seed = new Random().nextInt();

        Map testMap = new Map(0.5, seed);
        testMap.save_heightmap("Heightmap.png");
        testMap.save_climate("Climate.png");
        testMap.save_humidity("Humidity.png");
        testMap.save_temperature("Temperature.png");

        //testing noise generator to generate like resources map
        RandomNoise noise = new RandomNoise(140,100,3,0).make_multioctaved(1).minus(1.99).minus(-1).divide(0.001);
        noise.save_to_png("Resources.png");

        //testing game resources generator
        ResourcesGenerator bbblll = new ResourcesGenerator(seed);
        String[][][] sl = bbblll.toArray();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < sl.length; i++) {
            for (int j = 0; j < sl[0].length; j++) {
                StringBuilder r = new StringBuilder(" ");
                for (int k = 0; k < sl[0][0].length; k++) {
                    r.append(sl[i][j][k]);
                }
                res.append(r);
            }
            res.append("\n");
        }

        System.out.println(res.toString());
    }
}
