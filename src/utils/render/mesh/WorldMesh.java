package utils.render.mesh;

import utils.render.scene.WorldScene;

import java.util.Arrays;

/**
 * Mesh utilizado para renderizar una cantidad de objetos igual al área total del mundo. A contrario de <code>EntityMesh</code>,
 * este mesh no se instancia para cada uno de los objetos. Este mesh contiene todos los vértices directamente, que habrá
 * que añadir según se vayan generando los objetos.
 * @see Mesh
 */
public class WorldMesh extends Mesh {
    /**
     * Generador de coordenadas UV. Cada objeto puede tener unas coordenadas UV distintas.
     */
    private final UVCoordsGenerator UV_COORDS_RANDOMIZER;

    /**
     * Número de objetos que se han colocado en el mesh hasta el momento.
     */
    private int elementsCount = 0;

    /**
     * Últimos índices utilizados de los arrays de vérices y elementos.
     */
    private int previousVertexArrayPos = 0, previousElementArrayPos = 0;

    /**
     * @param capacity Capacidad que va a tener el mesh.
     * @param attributesSize Array que almacena los tamaños de los atributos, en bytes.
     * @param uvCoordsGenerator Generador de coordenadas UV.
     */
    public WorldMesh(int capacity, int[] attributesSize, UVCoordsGenerator uvCoordsGenerator) {
        super(attributesSize);
        this.UV_COORDS_RANDOMIZER = uvCoordsGenerator;
        this.vertexArray = new float[this.vertexSize *capacity *4];
        this.elementArray = new int[capacity *2 *3];
    }

    /**
     * Constructor que utiliza las coordenadas UV normales para todos los objetos.
     * @param capacity Capacidad que va a tener el mesh.
     * @param attributesSize Tamaños de los atributos, en bytes.
     */
    public WorldMesh(int capacity, int... attributesSize) {
        this(capacity, attributesSize, (posX, posY) -> new int[]{1, 1, 0, 0, 1, 0, 0, 1});
    }

    /**
     * Añade un objeto (con sus respectivos vértices) al mesh.
     * @param posX Posición del objeto en el eje X, en coordenadas in-game.
     * @param posY Posición del objeto en el eje Y, en coordenadas in-game.
     * @param sizeX Tamaño del objeto en el eje X, en coordenadas in-game.
     * @param sizeY Tamaño del objeto en el eje Y, en coordenadas in-game.
     * @param attributes Atributos del vértice.
     */
    public void addVertex(float posX, float posY, float sizeX, float sizeY, float... attributes) {
        float screenPosX = WorldScene.SPRITE_SIZE * posX, screenPosY = WorldScene.SPRITE_SIZE * posY;
        int[] uvCoords = this.UV_COORDS_RANDOMIZER.getUVCoords((int) posX, (int) posY);

        if (this.previousVertexArrayPos >= this.vertexArray.length) {
            this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos +this.vertexSize *4);
        }
        if (this.previousElementArrayPos >= this.elementArray.length) {
            this.elementArray = Arrays.copyOf(this.elementArray, this.previousElementArrayPos +6);
        }

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[0];
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[1];

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[2];
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[3];

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[4];
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[5];

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[6];
        this.vertexArray[this.previousVertexArrayPos++] = uvCoords[7];

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //Añadir elementos
        //Primer triangulo
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +2;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount;

        //Segundo triangulo
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +3;

        this.elementsCount += 4;
    }

    public void adjust() {
        this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos);
        this.elementArray = Arrays.copyOf(this.elementArray, this.previousElementArrayPos);
    }

    /**
     * Interfaz que define un método que devuelve las coordendadas UV.
     */
    public interface UVCoordsGenerator {
        /**
         * @return Array que contiene las coordenadas UV de los 4 vértices.
         */
        int[] getUVCoords(int posX, int posY);
    }
}
