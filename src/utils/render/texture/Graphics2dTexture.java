package utils.render.texture;

import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * Textura basada en <code>Graphics2D</code> nativos de Java. Cuanto más grande sea más recursos gasta, así que es preferible
 * generar gráficos 2D más pequeños y unirlos más tarde utilizando el resto de tipos de texturas.
 *
 * @see Graphics2D
 */
public class Graphics2dTexture extends Texture {
    /**
     * Buffer de imagen que almacena los gráficos 2D.
     */
    private final BufferedImage BUFFERED_IMAGE;

    /**
     * La instancia de <code>Graphics2D</code>.
     */
    private final Graphics2D GRAPHICS;

    /**
     * Identificador numérico de la textura.
     */
    private int textureId;

    /**
     * @param width Ancho de los gráficos.
     * @param height Alto de los gráficos.
     */
    public Graphics2dTexture(int width, int height) {
        this.BUFFERED_IMAGE = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.GRAPHICS = this.BUFFERED_IMAGE.createGraphics();
    }

    /**
     * @return La instancia de los gráficos 2D para poder dibujar sobre ellos.
     */
    public Graphics2D getGraphics() {
        return this.GRAPHICS;
    }

    /**
     * Dibuja los gráficos 2D en el buffer de imagen y genera la textura.
     * @return identificador numérico de la textura.
     */
    public int convert() {
        //Dibujamos los gráficos 2D en la imagen
        this.GRAPHICS.dispose();

        //Generamos la textura
        int textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D,
                0,
                GL20.GL_RGBA,
                this.BUFFERED_IMAGE.getWidth(),
                this.BUFFERED_IMAGE.getHeight(),
                0,
                GL20.GL_BGRA,
                GL20.GL_UNSIGNED_BYTE,
                this.BUFFERED_IMAGE.getRGB(
                        0,
                        0,
                        this.BUFFERED_IMAGE.getWidth(),
                        this.BUFFERED_IMAGE.getHeight(),
                        null,
                        0,
                        this.BUFFERED_IMAGE.getWidth()));

        //Repetimos la textura en todas direcciones
        List.of(GL20.GL_TEXTURE_WRAP_S, GL20.GL_TEXTURE_WRAP_T).forEach(textureRepeatDirection -> {
            GL20.glTexParameteri(GL20.GL_TEXTURE_2D, textureRepeatDirection, GL20.GL_REPEAT);
        });

        //Cuando hagamos la textura más grande o más pequeña se pixele.
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        return textureId;
    }

    @Override
    public void unbind() {
        GL20.glDeleteTextures(this.textureId);
        super.unbind();
    }

    @Override
    public void bind() {
        this.textureId = this.convert();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }
}
