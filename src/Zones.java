/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.Arrays;
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();

        int[][] graph = Graphs.slidesFrom(s);
        int[][] reversed = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int[] stack = new int[n];
        int[] next = new int[n];

        int orderSize = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;

            int top = 0;
            stack[top] = start;
            visited[start] = true;

            while (top >= 0) {
                int u = stack[top];

                if (next[u] < graph[u].length) {
                    int edge = graph[u][next[u]++];
                    int v = s.to()[edge];

                    if (!visited[v]) {
                        visited[v] = true;
                        stack[++top] = v;
                    }
                } else {
                    order[orderSize++] = u;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);

        int zoneCount = 0;

        for (int i = orderSize - 1; i >= 0; i--) {
            int start = order[i];

            if (zoneOf[start] != -1) continue;

            int top = 0;
            stack[top] = start;
            zoneOf[start] = zoneCount;

            while (top >= 0) {
                int u = stack[top--];

                for (int edge : reversed[u]) {
                    int v = s.from()[edge];

                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zoneCount;
                        stack[++top] = v;
                    }
                }
            }

            zoneCount++;
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int[] degree = new int[zoneCount];

        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            if (fromZone != toZone) {
                degree[fromZone]++;
            }
        }

        int[][] edges = new int[zoneCount][];

        for (int z = 0; z < zoneCount; z++) {
            edges[z] = new int[degree[z]];
        }

        int[] filled = new int[zoneCount];

        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            if (fromZone != toZone) {
                edges[fromZone][filled[fromZone]++] = i;
            }
        }

        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);

        long[] bestCost = new long[zoneCount];
        Arrays.fill(bestCost, Long.MAX_VALUE);
        bestCost[zoneOf[0]] = 0;

        for (int z = 0; z < zoneCount; z++) {
            for (int edge : edges[z]) {
                int nextZone = zoneOf[s.to()[edge]];

                stages[nextZone] = Math.max(
                        stages[nextZone], stages[z] + 1
                );

                if (bestCost[z] != Long.MAX_VALUE) {
                    long newCost = bestCost[z] + s.time()[edge];

                    if (newCost < bestCost[nextZone]) {
                        bestCost[nextZone] = newCost;
                    }
                }
            }
        }

        int longest = 0;

        for (int value : stages) {
            longest = Math.max(longest, value);
        }

        return new long[]{
                longest,
                bestCost[zoneOf[s.n() - 1]]
        };
    }
}
