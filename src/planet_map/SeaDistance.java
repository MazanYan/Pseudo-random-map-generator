package planet_map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class SeaDistance {
    private int height;
    private int width;
    private int[][] heightmap;
    public SeaDistance(Map mp) {
        height = mp.getHeight();
        width = mp.getWidth();
        heightmap = mp.toHeightmap();
    }

    public SeaDistance(int height, int width, int[][] heightmap) {
        this.height = height;
        this.width = width;
        this.heightmap = heightmap;
    }
    /**

     ===========================================Sea distance definition======================================================

     * Function that checks out the neighbours of a random point on the heightmap.
     * Firstly adds all eight neighbours (like it's a not border point) and then
     * removes every coordinate with
     * @param x - x coordinate of the base point
     * @param y - y coordinate of the base point
     * @return - coordinates of all the neighbours of the basic point
     */
    private int[][] neighbours(int x, int y) {
        ArrayList<int[]> neighbours = new ArrayList<>(Arrays.asList(new int[]{x-1,y-1}, new int[]{x,y-1},
                new int[]{x+1,y-1}, new int[]{x+1,y}, new int[]{x+1, y+1}, new int[]{x,y+1},
                new int[]{x-1,y+1}, new int[]{x-1, y}));
        ArrayList<int[]> to_remove = new ArrayList<>();
        for (int[] i : neighbours) {
            if (i[0] < 0 || i[1] < 0) {
                to_remove.add(i);
            }
            if (i[0] >= height || i[1] >= width) {
                to_remove.add(i);
            }
        }
        neighbours.removeAll(to_remove);
        int[][] res = new int[neighbours.size()][2];
        res = neighbours.toArray(res);
        return res;
    }

    /**
     * initially creates the hash-array of sea coast points (being the initial neighbours of an abstract zero point)
     * Uses Dijkstra's algorithm with initial visited points being in the previously mentioned hash-array
     */
    public int[][] define_sea_distances() {
        /*
         * Defining of initial points for the algorythm
         */
        int[][] distance_map = new int[height][width];
        /*
         * Structure of point in the hash table to compare: Point: int[3] = {distance_from_sea, x, y}
         * Priority queue sorts points by their distance from sea: points with smaller distance are forward,
         * with higher - backward
         */
        TreeMap<Double, SeaDistancePoint> to_visit = new TreeMap<>();
        TreeMap<Double, SeaDistancePoint> visited = new TreeMap<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (heightmap[i][j] == 0) {
                    distance_map[i][j] = 0;
                    continue;
                }
                if (heightmap[i][j] > 0) {
                    for (int[] elem : neighbours(i,j)) {
                        if (heightmap[elem[0]][elem[1]] == 0) {
                            distance_map[i][j] = 1;

                            SeaDistancePoint pt = new SeaDistancePoint(1,i,j);
                            to_visit.put(pt.encode(), pt);
                            break;
                        }
                        else {
                            distance_map[i][j] = height*width;
                        }
                    }
                }
            }
        }
        /*
         * Dijkstra's algorithm itself
         */
        while (!to_visit.isEmpty()) {
            double min_distance_key = to_visit.firstKey();
            SeaDistancePoint node_to_operate = to_visit.get(min_distance_key);
            int x = node_to_operate.get_x();
            int y = node_to_operate.get_y();
            int dist = node_to_operate.get_distance();
            int[][] neighbours = neighbours(x, y);
            for (int[] neighb : neighbours) {
                int neighb_x = neighb[0];
                int neighb_y = neighb[1];
                SeaDistancePoint operating_point = new SeaDistancePoint(distance_map[neighb_x][neighb_y], neighb_x, neighb_y);
                if (!visited.containsKey(operating_point.encode())) {
                    if (operating_point.get_distance() > dist + 1) {
                        operating_point.set_distance(node_to_operate.get_distance() + 1);
                        distance_map[operating_point.get_x()][operating_point.get_y()] = operating_point.get_distance();
                        to_visit.put(operating_point.encode(), operating_point);
                    }
                }
            }
            to_visit.remove(node_to_operate.encode());
            visited.put(node_to_operate.encode(), node_to_operate);
        }
        return distance_map;
    }

    /**
     * Class for a point while creating a distances from sea map to make attributes of
     * a point on the map collected in one place
     */
    private class SeaDistancePoint{
        private int x;
        private int y;
        private int distance;
        public SeaDistancePoint(int distance, int x, int y) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
        public int get_x() {
            return x;
        }
        public int get_y() {
            return y;
        }
        public void set_coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int get_distance() {
            return distance;
        }
        public void set_distance(int val) {
            distance = val;
        }
        /**
         * integer part is the distance
         * the fraction part consists of x coordinate and y coordinate
         * for {distance = 10, x = 50, y = 13} encode = 10.5013
         * @return - number encoded with higher mentioned algorythm
         */
        public double encode() {
            int x_digits_num = (int)Math.ceil(Math.log10(x+0.5));
            int y_digits_num = (int)Math.ceil(Math.log10(y+0.5));
            return distance + (double)x/Math.pow(10,x_digits_num) + (double)y/Math.pow(10,y_digits_num)/Math.pow(10,x_digits_num);
        }
        public int hashCode() {
            int x_digits_num = (int)Math.ceil(Math.log10(x+0.5));
            int y_digits_num = (int)Math.ceil(Math.log10(y+0.5));
            return (int)(encode()*Math.pow(10,x_digits_num)*Math.pow(10,y_digits_num));
        }
    }
}
