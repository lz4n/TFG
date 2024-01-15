package utils.render.texture;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;
import utils.Logger;
import utils.render.Shader;
import utils.render.mesh.SingleObjectMesh;
import utils.render.scene.WorldScene;
import world.location.Location;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Representa una textura. Permite aplicar y desaplicar una textura en la <code>GPU</code>.
 *
 * @author Izan
 */
public abstract class Texture {
    /**
     * Almacena el tamaño de la textura en píxeles.
     */
    private Vector2i textureSize = new Vector2i(0, 0);

    /**
     * Cambia el tamaño de la textura.
     * @param textureSize Nuevo tamaño de textura, en pixeles.
     */
    protected void setTextureSize(Vector2i textureSize) {
        this.textureSize = textureSize;
    }

    /**
     * Sube la textura a la <code>GPU</code>.
     */
    public void bind() {
        this.bind(0);
    }

    public void bind(int unit) {
        GL20.glActiveTexture(GL13.GL_TEXTURE0 +unit);
    }

    /**
     * Limpia la textura de la <code>GPU</code>, y limpia la memoria.<br>
     * <b>IMPORTANTE</b>: la limpieza de memoria la implementa cada clase de forma individual.
     */
    public static void unbind() {
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    /**
     * @return Devuelve el identificador numérico de la textura. Si es animada devuelve el identificador de la textura
     * correspondiente el frame actual.
     */
    public abstract int getTextureId();

    /**
     * @return Tamaño de la textura en píxeles.
     */
    public Vector2i getSize() {
        return new Vector2i(this.textureSize);
    }

    /**
     * Instancia una textura en cierta posición y con un tamaño determinado.
     * @param shader Shader encargado de la instanciación.
     * @param posX Posición en el eje X en la que se va a instanciar.
     * @param posY Posición en el eje Y en la que se va a instanciar.
     * @param sizeX Tamaño en el eje X de la instancia.
     * @param sizeY Tamaño en el eje Y de la instancia.
     */
    public void draw(Shader shader, float posX, float posY, float sizeX, float sizeY) {
        if (shader.supportsInstantiation()) {
            shader.upload2f("uPosition", posX, posY);
            shader.upload2f("uSize", sizeX, sizeY);

            this.bind();
            ARBVertexArrayObject.glBindVertexArray(SingleObjectMesh.SINGLE_OBJECT_MESH.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, SingleObjectMesh.SINGLE_OBJECT_MESH.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
        } else {
            Logger.sendMessage("El shader %s no puede dibujar (la textura %d) mediante instanciación.", Logger.LogMessageType.FATAL, shader, this.getTextureId());
        }
    }

    /**
     * Instancia una textura en cierta posición y con un tamaño determinado (ambos en unidades in-game).
     * @param shader Shader encargado de la instanciacion.
     * @param location Posición in-game donde se va a realizar la instanciacion.
     * @param size Tamaño de la isntancia, en unidades in-game.
     */
    public void draw(Shader shader, Location location, Vector2f size) {
        location = location.clone().getInScreenCoords();
        size.mul(WorldScene.SPRITE_SIZE);
        this.draw(shader,
                location.getX(),
                location.getY(),
                size.x(),
                size.y()
                );
    }

    /**
     * Instancia una textura en una posición específica, pero con el tamaño de la textura.
     * @param shader Shader encargado de la isntanciación.
     * @param location Posición en la que se va a realizar la instancia.
     */
    public void draw(Shader shader, Location location) {
        location = location.clone().getInScreenCoords();
        this.draw(shader,
                location.getX(),
                location.getY(),
                this.textureSize.x(),
                this.textureSize.y()
        );
    }

    /**
     * Genera una textura a partir de una imagen .png con 32bits de color.
     * @param path ruta a la textura.
     * @return identificador numérico de la textura.
     */
    protected final int generateSprite(String path, int param) {
        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1), channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image;
        int textureId = GL20.glGenTextures();

        //Generamos la textura
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);

        //Repetimos la textura en todas direcciones
        //List.of(GL20.GL_TEXTURE_WRAP_S, GL20.GL_TEXTURE_WRAP_T).forEach(textureRepeatDirection -> GL20.glTexParameteri(GL20.GL_TEXTURE_2D, textureRepeatDirection, GL20.GL_CLAMP_TO_EDGE));

        //Cuando hagamos la textura más grande o más pequeña se pixele.
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        //Envoltura vertical y horizontal
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, param);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, param);

        image = STBImage.stbi_load(path, width, height, channels, 0);
        if (image != null) {
            //Para poder utilizar imágenes con 32 y 24 bits de color.
            int format = (channels.get(0) == 4) ? GL20.GL_RGBA : GL20.GL_RGB;

            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL20.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
        } else {
            Logger.sendMessage("No se ha podido cargar la textura '%s'.", Logger.LogMessageType.WARNING, path);
        }

        return textureId;
    }
}
