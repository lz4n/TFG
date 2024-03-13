package npc;

import org.joml.Vector2d;
import world.World;
import world.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Node {
    private int x, y;
    private double pathTotal, pathHeuristic, pathWalk;
    private Node parent; //Este será el nodo padre de la ruta.

    /*
    EXPLICACIÓN BÁSICA:
    -pathTotal = es la distancia total recorrida entre la estimada y la heurística.

    -La heurística es la distancia estimada hacia el destino.

    -pathWalk es la distancia que se tiene en cuenta desde el nodo inicio hasta el punto
    por el que irá el NPC. Así como hace GoogleMaps.
    */

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        //Inicializaremos todo a 0 desde un inicio
        this.pathTotal = 0;
        this.pathHeuristic = 0;
        this.pathWalk = 0;
        this.parent = null; //También inicializaremos el parent a nulo con el constructor.
    }

    //Usaremos este método para poder coger el nodo más cercano/con menos coste.
    public static Node lowerCost(List<Node> openList) {
        //Creamos un nodo con el valor máximo posible para hacer una comparación
        double dblMayorCost = Double.POSITIVE_INFINITY;
        Node ndLowCost = null;

        /*Hacemos un for-each para ir recorriendo la lista e ir comparando los valores
        hasta encontrar el más pequeño, será con ese con el que nos quedaremos.
        */
        for (Node key : openList) {
            if (key.getPathTotal() < dblMayorCost) {
                //Le asignamos el key a lowCost para continuar la comparación.
                ndLowCost = key;
                dblMayorCost = key.getPathTotal(); //Conseguimos el valor del nodo key al que estamos apuntando y lo guardamos para la comparación siguiente.
            }
        }
        return ndLowCost;
    }

    //Recreamos la ruta una vez ya terminada, la posición final del nodo pasará a ser la principal.
    public static List<Vector2d> recreatePath(Node ndActual) {
        List<Vector2d> path = new ArrayList<>();
        while (ndActual != null) {
            int xActual = ndActual.getX();
            int yActual = ndActual.getY();
            path.add(new Vector2d(xActual, yActual)); //Creamos la nueva ruta con el vector.
            ndActual = ndActual.getParent(); //Guardamos el end del nodo anterior.
        }
        /*
        Ahora, tendremos que invertir el orden de los elementos de la lista Path,
        esto se hace para que el final sea el inicio y podamos reconstruir la ruta inversamente.
        Se hace siguiendo los nodos padres.
        */
        Collections.reverse(path); //Invertimos el orden jojojojo.
        return path;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getPathTotal() {
        return pathTotal;
    }

    public double getPathHeuristic() {
        return pathHeuristic;
    }

    public double getPathWalk() {
        return pathWalk;
    }

    public Node getParent() {
        return parent;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPathTotal(double pathTotal) {
        this.pathTotal = pathTotal;
    }

    public void setPathHeuristic(double pathHeuristic) {
        this.pathHeuristic = pathHeuristic;
    }

    public void setPathWalk(double pathWalk) {
        this.pathWalk = pathWalk;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
