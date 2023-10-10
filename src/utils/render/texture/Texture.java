package utils.render.texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;

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
     * Sube la textura a la <code>GPU</code>.
     */
    public abstract void bind();

    /**
     * Limpia la textura de la <code>GPU</code>, y limpia la memoria.<br>
     * <b>IMPORTANTE</b>: la limpieza de memoria la implementa cada clase de forma individual.
     */
    public void unbind() {
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    public abstract int getTextureId();

    /**
     * Genera una textura a partir de una imagen .png con 32bits de color.
     * @param path ruta a la textura.
     * @return identificador numérico de la textura.
     */
    protected final int generateSprite(String path) {
        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1), channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image;
        int textureId = GL20.glGenTextures();

        //Generamos la textura
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);

        //Repetimos la textura en todas direcciones
        List.of(GL20.GL_TEXTURE_WRAP_S, GL20.GL_TEXTURE_WRAP_T).forEach(textureRepeatDirection -> GL20.glTexParameteri(GL20.GL_TEXTURE_2D, textureRepeatDirection, GL20.GL_CLAMP_TO_EDGE));

        //Cuando hagamos la textura más grande o más pequeña se pixele.
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        image = STBImage.stbi_load(path, width, height, channels, 0);
        if (image != null) {
            //Para poder utilizar imágenes con 32 y 24 bits de color.
            int format = (channels.get(0) == 4) ? GL20.GL_RGBA : GL20.GL_RGB;

            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL20.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
        } else {
            System.err.printf("No se ha podido cargar la textura '%s'.\n", path);
        }

        return textureId;
    }
}
