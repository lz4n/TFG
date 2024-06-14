package game.utils.render.mesh;

import game.utils.render.scene.WorldScene;
import org.lwjgl.opengl.GL20;

import java.util.Arrays;

/**
 * Mesh utilizado para renderizar una cantidad de objetos igual al área total del mundo. A contrario de <code>EntityMesh</code>,
 * este mesh no se instancia para cada uno de los objetos. Este mesh contiene todos los vértices directamente, que habrá
 * que añadir según se vayan generando los objetos.
 * @see Mesh
 */
public class WorldMesh extends Mesh {
    private final UVCoordsGenerator UV_COORDS_RANDOMIZER;
    private int previousVertexArrayPos = 0;

    /**
     * @param capacity Número de vértices que va a tener el mesh.
     * @param attributesSize Array que almacena los tamaños de los atributos, en bytes.
     * @param uvCoordsGenerator Generador de coordenadas UV.
     */
    public WorldMesh(int capacity, int[] attributesSize, UVCoordsGenerator uvCoordsGenerator) {
        super(attributesSize);
        this.UV_COORDS_RANDOMIZER = uvCoordsGenerator;
        this.vertexArray = new float[this.vertexSize *capacity *6];
    }

    /**
     * Constructor que utiliza las coordenadas UV normales para todos los objetos.
     * @param capacity Número de vértices que va a tener el mesh.
     * @param attributesSize Tamaños de los atributos, en bytes.
     */
    public WorldMesh(int capacity, int... attributesSize) {
        this(capacity, attributesSize, (posX, posY) -> new int[]{0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1});
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

        //1er vértice: arriba izquierda
        this.addVertex(
                screenPosX,
                screenPosY + WorldScene.SPRITE_SIZE *sizeY,
                uvCoords[0],
                uvCoords[1],
                attributes
        );

        //2º vértice: abajo derecha
        this.addVertex(
                screenPosX + WorldScene.SPRITE_SIZE *sizeX,
                screenPosY,
                uvCoords[2],
                uvCoords[3],
                attributes
        );

        //3er vértice: abajo izquierda
        this.addVertex(
                screenPosX,
                screenPosY,
                uvCoords[4],
                uvCoords[5],
                attributes
        );

        //4º vértice: arriba izquierda
        this.addVertex(
                screenPosX,
                screenPosY + WorldScene.SPRITE_SIZE *sizeY,
                uvCoords[6],
                uvCoords[7],
                attributes
        );

        //5º vértice: arriba derecha
        this.addVertex(
                screenPosX + WorldScene.SPRITE_SIZE *sizeX,
                screenPosY + WorldScene.SPRITE_SIZE *sizeY,
                uvCoords[8],
                uvCoords[9],
                attributes
        );

        //6º vértice: abajo derecha
        this.addVertex(
                screenPosX + WorldScene.SPRITE_SIZE * sizeX,
                screenPosY,
                uvCoords[10],
                uvCoords[11],
                attributes
        );

    }

    private void addVertex(float screenPosX, float screenPosY, int uvCoordsX, int uvCoordsY, float[] attributes) {
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = uvCoordsX;
        this.vertexArray[this.previousVertexArrayPos++] = uvCoordsY;

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }
    }

    /**
     * Comprueba si el Mesh tiene elementos o no.
     * @return Si el Mesh tiene elementos o no.
     */
    public boolean isEmpty() {
        return this.previousVertexArrayPos == 0;
    }

    /**
     * Ajusta los arreglos de vértices y elementos del mesh a sus tamaños actuales.
     * Esto implica recortar los arreglos para que coincidan exactamente con la cantidad
     * de vértices y elementos utilizados actualmente.
     */
    public void adjust() {
        this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos);
    }

    /**
     * Interfaz que define un método que devuelve las coordendadas UV.
     */
    public interface UVCoordsGenerator {
        /**
         * @return Array que contiene las coordenadas UV de los 4 vértices.
         *
         * @param posX Posición en el eje X en el mundo del objeto a renderizar.
         * @param posX Posición en el eje X en el mundo del objeto a renderizar.
         */
        int[] getUVCoords(int posX, int posY);
    }

    @Override
    protected int getRenderMode() {
        return GL20.GL_TRIANGLES;
    }
}
