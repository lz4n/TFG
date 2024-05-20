package npc;

import main.Main;
import org.joml.Vector2d; //Es la clase Vector2d qué utilizaremos
import world.World;
import world.location.Location;
import world.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Pathfinding {

    //Creamos un método que encuentre el path, dentro ocurrirá la magia: algortimo a star.
    public List<Vector2d> findPath(Location start, Location goal) { //La lista la utilizaremos para crear el path a través del algortimo star A.
        List<Vector2d> path = new ArrayList<>(); //Arraylist porque jugamos con arrays juju.

        /*
        Habrá que encontrar una ruta desde 'start' hasta 'end'.
         COSAS A TENER EN CUENTA PARA QUE FUNCIONE BIEN:
         -Coordenadas y límites del mundo (se me ha ocurrido que algunos patos si se encuentran en x location tenga una
         animación de caminar por el path diferente, por ejemplo, si están en el agua que naden o cosas así.
         -Posición de los obstáculos (deben evitar pasar por encima de árboles, casas y otros patos
         (se consideran así mismos un obstáculo).
        */

        //Marcamos las coordenadas y las volvemos una ubicación en el mapa.
        int startX = (int) start.getX(); //Marca el start de x e y
        int startY = (int) start.getY();
        int goalX = (int) goal.getX(); //Marca el end de x e y
        int goalY = (int) goal.getY();

        //Ahora implementamos el cálculo de la ruta con el algortimo de a star:
        /*En primer lugar, tendremos que crear dos nodos, uno el de la ubicación actual y otro con la ubicación final
          como si fuese GoogleMaps (para entenderlo mejor)
        */
        Node ndStart = new Node(startX, startY);
        Node ndEnd = new Node(goalX, goalY);

        /*Ahora, vamos a meter los nodos en dos listas propias del algoritmo a star.
        -listaAbierta: Esta lista contiene los nodos que el algortimo todavía no ha explorado.
        Se ordenan mediante el pathTotal, esta lista contiene los nodos candidatos, se termina
        eligiendo el nodo con el pathTotal más bajo.

        -listaCerrada: Esta lista contiene los nodos que ya han sido seleccionados y recorridos.
        Su principal función es que el algortimo a star evite explorar el mismo nodo más de una vez.
        */

        List<Node> openList = new ArrayList<>();
        List<Node> closeList = new ArrayList<>();

        //Tendremos que añadir el nodo de empiece sí o sí a la listaAbierta para que empiece la magia.
        openList.add(ndStart);

        //Comenzamos dentro de la lista abierta la búsqueda de los nodos.
        while (!openList.isEmpty()) {
            System.out.println(openList.size());

            Node ndActual = Node.lowerCost(openList);
            openList.remove(ndActual);
            closeList.add(ndActual);

            if (ndActual.equals(ndEnd)) {
                path = Node.recreatePath(ndActual);
                break;
            }

            List<Node> listNeighbors = getNeighbors(ndActual);

            for (Node key : listNeighbors) {
                if (closeList.contains(key))
                    continue;

                double costPosNeigh = ndActual.getPathWalk() + calculatePathWalk(ndActual, key);

                if (!openList.contains(key) || costPosNeigh < key.getPathWalk()) {
                    key.setParent(ndActual);
                    key.setPathWalk(costPosNeigh);
                    key.setPathHeuristic(calculateHeuristic(key, ndEnd));
                    key.setPathTotal(key.getPathWalk() + key.getPathHeuristic());

                    if (!openList.contains(key)) {
                        openList.add(key);
                    }
                }
            }
        }
        return path;
    }

    private List<Node> getNeighbors(Node ndActual) {
        List<Node> listNeighbors = new ArrayList<Node>();
        int x = ndActual.getX();
        int y = ndActual.getY();

        //Definimos las coordenadas de los posibles vecinos, de la siguiente forma, pues sólo queremos ver los al rededores:
        int dx[] = {-1, 0, 1, -1, 1, -1, 0, 1};
        int dy[] = {-1, -1, -1, 0, 0, 1, 1, 1};

        //Creamos los posibles nodos vecinos:
        for (int i = 0; i < dy.length; i++) {
            int xNeightbor = x + dx[i];
            int yNeightbor = y + dy[i]; //Le asignamos la posición actual sumándosela para ver cómo va quedando.

            /* Ahora, tendremos que llamar a un método por el cuál calcule si los datos se encuentran
            dentro de los límites del mapa y si presentan una ubicación válida (no hay obstáculos) de
            por medio.
            */
            if (isLocationValid(xNeightbor, yNeightbor))
                listNeighbors.add(new Node(xNeightbor, yNeightbor)); //Añadimos el nodo vecino a la lista como posible candidato para la openList.
        }
        return listNeighbors;
    }


    private boolean isLocationValid(int xNeightbor, int yNeightbor) {
        if (xNeightbor >= 0 && xNeightbor < Main.world.getSize() && yNeightbor >= 0 && yNeightbor < Main.world.getSize()) {
            Terrain terrain = Main.world.getTerrain(xNeightbor, yNeightbor);
            return terrain != null && terrain.isPassable();
        }
        return false;
    }

    private double calculatePathWalk(Node ndActual, Node key) {
        //Los movimientos diagonales tendrán un costo mucho mayor, pero no pasa nada porque es un juego a a bit muy mono.
        int xLow = Math.abs(key.getX() - ndActual.getX()); //Calcula las diferencias entre el nodo actual y el vecino.
        int yLow = Math.abs(key.getY() - ndActual.getY());
        double costDiagonal = Math.sqrt(2); //Representará la distancia euclidiana, es una distancia utilizada en el algoritmo a star.

        if (xLow > yLow) //Está asumiendo que el nodo vecino se encuentra en una posición diagonal.
            return costDiagonal* yLow+ (xLow - yLow);
        else
            return costDiagonal * xLow + (yLow - xLow);
    }

    private double calculateHeuristic(Node key, Node ndEnd) {
        double xHeuristic = Math.abs(key.getX() - ndEnd.getX());
        double yHeuristic = Math.abs(key.getY() - ndEnd.getY());
        return xHeuristic + yHeuristic;
        //MATH ABS se utiliza para obtener SIEMPRE el valor absoluto de un número pues queremos obtener la distancia más corta y queremos que nos lo cuente bien y no con negativos, porque si no, siempre se iría hacia el eje negativo y nunca llegaría al destino.
    }
}