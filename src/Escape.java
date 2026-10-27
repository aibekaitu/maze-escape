/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
import java.util.Arrays;
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int[][] open = Graphs.open(w, broken);

        // Count dead ends
        int deadEnds = 0;

        for (int i = 0; i < n; i++) {
            if (open[i].length == 1) {
                deadEnds++;
            }
        }

        // Prepare DFS
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int top = 0;
        stack[0] = 0;

        int visitedCount = 0;
        int exit = n - 1;

        // Iterative DFS
        while (top >= 0) {
            int u = stack[top--];

            if (visited[u]) {
                continue;
            }

            visited[u] = true;
            visitedCount++;

            if (u == exit) {
                break;
            }

            // Reverse order: left, down, right, up
            for (int d = 3; d >= 0; d--) {
                int v;

                if (d == 0) {
                    if (u < cols) continue;
                    v = u - cols;
                } else if (d == 1) {
                    if (u % cols == cols - 1) continue;
                    v = u + 1;
                } else if (d == 2) {
                    if (u >= n - cols) continue;
                    v = u + cols;
                } else {
                    if (u % cols == 0) continue;
                    v = u - 1;
                }

                if (visited[v]) continue;

                for (int next : open[u]) {
                    if (next == v) {
                        parent[v] = u;
                        stack[++top] = v;
                        break;
                    }
                }
            }
        }

        // Reconstruct the path
        if (!visited[exit]) {
            return new Result(new int[0], visitedCount, deadEnds);
        }

        int length = 0;

        for (int v = exit; v != -1; v = parent[v]) {
            length++;
        }

        int[] path = new int[length];

        for (int v = exit; v != -1; v = parent[v]) {
            path[--length] = v;
        }

        return new Result(path, visitedCount, deadEnds);
    }
}
