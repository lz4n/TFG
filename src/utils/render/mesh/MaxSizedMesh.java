package utils.render.mesh;

import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.util.Arrays;

/**
 * <code>Mesh</code>  de tamaño fijo. Si se supera el tamaño establecido lanza una excepción. Lo que si se puede hacer
 * es recortar su tamaño una vez se han incluido todos los objetos.
 * @see Mesh
 *
 * @author Izan
 */
public class MaxSizedMesh extends Mesh {

    protected int previousVertexArrayPos = 0, previousElementArrayPos = 0;

    /**
     * @param maxSize Tamaño máximo del <code>mesh</code>.
     * @param texture Textura correspondiente al <code>mesh</code>.
     */
    public MaxSizedMesh(int maxSize, Texture texture) {
        super(texture);
        this.vertexArray = new float[maxSize *9*4];
        this.elementArray = new int[maxSize *2*3];
    }

    @Override
    public void addVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 1f;
        this.vertexArray[this.previousVertexArrayPos++] = 1f;

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 0f;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 1f;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 0f;
        this.vertexArray[this.previousVertexArrayPos++] = 1f;

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

    /**
     * Recorta la parte de los arrays que no han sido utilizadas.
     */
    public void adjust() {
        if (this.previousVertexArrayPos > 0) {
            this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos);
            this.elementArray = Arrays.copyOf(this.elementArray, this.previousElementArrayPos);
        }
    }
}
