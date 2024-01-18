package utils.render.texture;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;
import utils.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Representa una textura fija.
 *
 * @author Izan
 */
public class StaticTexture extends Texture implements CacheTexture {
    /**
     * Ruta de la textura.
     */
    private final String PATH;

    /**
     * Tipo de envoltura.
     */
    private final int PARAM;

    /**
     * Identificador numérico de la textura.
     */
    private int textureId;

    /**
     * @param path Ruta al archivo .png de la textura.
     * @param param Tipo de envoltura de la textura.
     */
    StaticTexture(String path, int param) {
        this.PATH = path;
        this.PARAM = param;

        try {
            BufferedImage bufferedTexture = ImageIO.read(new File(this.PATH));
            if (bufferedTexture != null) {
                this.setTextureSize(new Vector2i(bufferedTexture.getWidth(), bufferedTexture.getHeight()));
            }
        } catch (IOException exception) {
            Logger.sendMessage("No se ha podido leer la textura \"%s\".", Logger.LogMessageType.WARNING, this.PATH);
        }
    }

    /**
     * Establece la envoltura en <code>GL20.GL_CLAMP_TO_EDGE</code> por defecto.
     * @param path Ruta al archivo .png de la textura.
     * @see StaticTexture#StaticTexture(String, int)
     */
    protected StaticTexture(String path) {
        this(path, GL20.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void remove() {
        GL20.glDeleteTextures(this.textureId);
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public void bind(int unit) {
        super.bind(unit);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public void init() {
        this.textureId = this.generateSprite(this.PATH, this.PARAM);
    }

    @Override
    public String toString() {
        return String.format("StaticTexture(path=%s)", this.PATH);
    }
}
