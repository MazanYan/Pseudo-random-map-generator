import planet_map.Map;
import planet_map.RandomNoise;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int seed = new Random().nextInt();

        Map s = new Map(0.5, seed);
        s.save_heightmap("awiourespdt.png");
        s.save_climate("Shalala.png");
        s.save_humidity("Renji.png");
        s.save_temperature("Bre.png");

        RandomNoise renji = new RandomNoise(140,100,3,0).make_multioctaved(1).minus(1.99).minus(-1).divide(0.001);
        renji.save_to_png("Renji Ab.png");
    }
}
