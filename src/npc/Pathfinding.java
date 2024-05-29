package npc;

import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.*;

public class Pathfinding {

    public List<Node> findPath(Vector2f start, Vector2f goal) {
        List<Node> path = new ArrayList<>();

        int startX = (int) start.x;
        int startY = (int) start.y;
        int goalX = (int) goal.x;
        int goalY = (int) goal.y;

        Node ndStart = new Node(startX, startY);
        Node ndEnd = new Node(goalX, goalY);

        List<Node> openList = new ArrayList<>();
        List<Node> closeList = new ArrayList<>();

        openList.add(ndStart);

        while (!openList.isEmpty()) {
            Node ndActual = openList.remove(0);
            closeList.add(ndActual);

            if (ndActual.equals(ndEnd)) {
                path = recreatePath(ndActual);
                break;
            }

            List<Node> listNeighbors = getNeighbors(ndActual);

            for (Node neighbor : listNeighbors) {
                if (closeList.contains(neighbor))
                    continue;

                double tentative_gScore = ndActual.getPathWalk() + calculatePathWalk(ndActual, neighbor);

                if (!openList.contains(neighbor) || tentative_gScore < neighbor.getPathWalk()) {
                    neighbor.setParent(ndActual);
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
        return path;
    }

    private List<Node> recreatePath(Node ndEnd) {
        List<Node> path = new ArrayList<>();
        Node current = ndEnd;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private List<Node> getNeighbors(Node node) {
        // Este método debe ser implementado para obtener los nodos vecinos.
        // Aquí se incluye un ejemplo genérico.
        List<Node> neighbors = new ArrayList<>();
        // Ejemplo de vecinos en una cuadrícula de 4 direcciones (arriba, abajo, izquierda, derecha)
        neighbors.add(new Node((int) (node.getX() + 1), (int) node.getY()));
        neighbors.add(new Node((int) (node.getX() - 1), (int) node.getY()));
        neighbors.add(new Node((int) node.getX(), (int) (node.getY() + 1)));
        neighbors.add(new Node((int) node.getX(), (int) (node.getY() - 1)));
        return neighbors;
    }

    private double calculatePathWalk(Node from, Node to) {
        // Calcular el costo de moverse de un nodo a otro.
        return 1.0; // Ejemplo simple, en la práctica se debería calcular basado en la distancia real.
    }

    private double calculateHeuristic(Node from, Node to) {
        // Calcular la heurística (usualmente la distancia manhattan).
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }
}