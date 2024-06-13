package game.npc;


import game.world.location.Location;

import java.util.*;

/**
 * Clase que implementa el algoritmo de búsqueda de caminos (Pathfinding).
 * Utiliza el patrón Singleton para asegurar que solo exista una instancia de Pathfinding.
 */
public class Pathfinding {
    private static final Pathfinding PATHFINDER = new Pathfinding();

    private Pathfinding() {}

    /**
     * Obtiene la instancia única de Pathfinding.
     *
     * @return la instancia única de Pathfinding.
     */
    public static Pathfinding getPathfinder() {
        return Pathfinding.PATHFINDER;
    }

    /**
     * Encuentra el camino desde una ubicación inicial hasta una ubicación objetivo.
     *
     * @param start la ubicación inicial.
     * @param goal la ubicación objetivo.
     * @return una lista de ubicaciones que representa el camino desde el inicio hasta el objetivo.
     */
    public List<Location> findPath(Location start, Location goal) {
        Node ndStart = new Node(start);
        Node ndEnd = new Node(goal);

        List<Node> openList = new ArrayList<>();
        List<Node> closeList = new ArrayList<>();

        openList.add(ndStart);

        var ref = new Object() {
            Node ndActual;
        };

        float tentative_gScore = 0;
        while (!openList.isEmpty()) {
            ref.ndActual = openList.removeFirst();
            closeList.add(ref.ndActual);

            if (ref.ndActual.equals(ndEnd)) {
                return recreatePath(ref.ndActual);
            }

            for (Node neighbor : getNeighbors(ref.ndActual)) {
                if (closeList.contains(neighbor))
                    continue;

                tentative_gScore = ref.ndActual.getPathWalk() + calculatePathWalk(ref.ndActual, neighbor);

                if (!openList.contains(neighbor) || tentative_gScore < neighbor.getPathWalk()) {
                    neighbor.setParent(ref.ndActual);
                    neighbor.setPathWalk(tentative_gScore);
                    neighbor.setPathHeuristic(calculateHeuristic(neighbor, ndEnd));
                    neighbor.setPathTotal(neighbor.getPathWalk() + neighbor.getPathHeuristic());

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }

            openList.sort(Comparator.comparingDouble(Node::getPathTotal));
        }
        return new ArrayList<>();
    }

    private List<Location> recreatePath(Node ndEnd) {

        List<Location> path = new ArrayList<>();
        while (ndEnd != null) {
            path.add(ndEnd);
            ndEnd = ndEnd.getParent();
        }
        Collections.reverse(path);
        return path;

    }

    private List<Node> getNeighbors(Node node) {
        //Este método debe ser implementado para obtener los nodos vecinos.
        //Aquí se incluye un ejemplo genérico.
        List<Node> neighbors = new ArrayList<>();
        // Ejemplo de vecinos en una cuadrícula de 4 direcciones (arriba, abajo, izquierda, derecha)
        neighbors.add(new Node((int) (node.getX() + 1), (int) node.getY()));
        neighbors.add(new Node((int) (node.getX() - 1), (int) node.getY()));
        neighbors.add(new Node((int) node.getX(), (int) (node.getY() + 1)));
        neighbors.add(new Node((int) node.getX(), (int) (node.getY() - 1)));
        return neighbors;
    }


    private float calculatePathWalk(Node from, Node to) {
        //Calcular el costo de moverse de un nodo a otro.
        return to.getPathCost(); //Ejemplo simple, en la práctica se debería calcular basado en la distancia real.
    }

    private float calculateHeuristic(Node from, Node to) {
        //Calcular la heurística (usualmente la distancia manhattan).
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }
}