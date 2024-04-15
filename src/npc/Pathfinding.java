package npc;

import org.joml.Vector2d; //Es la clase Vector2d qué utilizaremos
import world.World;
import world.location.Location;
import world.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Pathfinding {
    private World world;

    public Pathfinding() {
    }
    public Pathfinding(World world) { //Creamos el constructor Pathfinding donde le añadimos el mundo como atributo pues es la base que utilizaremos.
        this.world = world;
    }

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

        List<Node> openList = new ArrayList<Node>();
        List<Node> closeList = new ArrayList<Node>();

        //Tendremos que añadir el nodo de empiece sí o sí a la listaAbierta para que empiece la magia.
        openList.add(ndStart);

        //Comenzamos dentro de la lista abierta la búsqueda de los nodos.
        while (!openList.isEmpty()) {
            Node ndActual = Node.lowerCost(openList);
            //Como ya lo tenemos, ahora, lo que hacemos es eliminar el nodo de la lista abierta.
            openList.remove(ndActual);
            //Se lo añadimos a la cerrada para no volverlo a recorrer nunca más.
            closeList.add(ndActual);

            //Si se llega al end, lo que hacemos es volver a reconstruir una nueva ruta y terminamos el programa, se tendrá que volver a repetir con los nuevos valores.
            if (ndActual.equals(ndEnd)) {
                path = Node.recreatePath(ndActual); //Le pasamos el nodo actual donde se encuentra el NPC para recalcular la ruta desde allí.
                break;
            }
            //Ahora, vamos a buscar los nodos candidatos, es decir, los vecinos que podrán ser seleccionados o no.
            List<Node> listNeighbors = getNeighbors(ndActual);

            //Vamos a ver si el nodo vecino se encuentra en la closeList, si lo estuviese, tendríamos que ignorarlo por completo.
            for (Node key : listNeighbors) {
                if (closeList.contains(key))
                    continue; //Lo ignoramos simplemente.
                //Ahora, tendremos que calcular el costo de la distancia recorrida hacia el nodo vecino:
                double costPosNeigh = ndActual.getPathWalk() + calculatePathWalk(ndActual, key);

                //Si el vecino ahora no se encuentra en el openList o tiene un costo de distancia recorrida mucho menor, haremos:
                if (!openList.contains(key) || costPosNeigh < key.getPathWalk()) {
                    //Establecemos al nodo vecino como actual y actualizamos todos sus costes y parámetros.
                    key.setParent(ndActual);
                    key.setPathWalk(costPosNeigh);
                    key.setPathHeuristic(calculateHeuristic(key, ndEnd));
                    key.setPathTotal(key.getPathWalk() + key.getPathHeuristic()); //Le sumamos el total :)

                    //Por último, si el vecino no se encuentra el la lista abierta, lo tendremos que añadir manualmente.
                    openList.add(key);
                }

            }
        }
        return path;
    }

    public List<Node> getNeighbors(Node ndActual) {
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


    public boolean isLocationValid(int xNeightbor, int yNeightbor) {
        if (xNeightbor >= 0 && xNeightbor < world.getSize() && yNeightbor >= 0 && yNeightbor < world.getSize()) {
            Terrain terrain = world.getTerrain(xNeightbor, yNeightbor);
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