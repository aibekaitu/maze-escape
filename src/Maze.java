/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
public class Maze {


    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];

            if (dsu.union(a, b)) {
                broken[count] = k;
                count++;
            }
        }

        return broken;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int i = 0; i < w.count(); i++) {
            order[i] = i;
        }

        Arrays.sort(order, Comparator.comparingInt(i -> w.cost()[i]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count] = k;
                count++;

                if (count == broken.length) {
                    break;
                }
            }
        }

        return broken;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();

        int[] degree = new int[n];

        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }

        int[][] around = new int[n][];

        for (int i = 0; i < n; i++) {
            around[i] = new int[degree[i]];
        }

        int[] filled = new int[n];

        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];

            around[a][filled[a]++] = k;
            around[b][filled[b]++] = k;
        }

        boolean[] visited = new boolean[n];

        PriorityQueue<int[]> pq =
                new PriorityQueue<>(Comparator.comparingInt(e -> e[0]));

        pq.add(new int[]{0, 0});

        long total = 0;
        int visitedCount = 0;

        while (!pq.isEmpty() && visitedCount < n) {
            int[] edge = pq.poll();

            int cost = edge[0];
            int cell = edge[1];

            if (visited[cell]) {
                continue;
            }

            visited[cell] = true;
            visitedCount++;
            total += cost;

            for (int k : around[cell]) {
                int other = (w.a()[k] == cell)
                        ? w.b()[k]
                        : w.a()[k];

                if (!visited[other]) {
                    pq.add(new int[]{w.cost()[k], other});
                }
            }
        }

        return total;
    }
}
