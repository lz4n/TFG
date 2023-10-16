package utils.render.texture;

import org.lwjgl.opengl.GL20;

/**
 * Representa una textura fija.
 *
 * @author Izan
 */
public class StaticTexture extends Texture implements AtlasTexture {
    /**
     * Ruta de la textura.
     */
    private final String PATH;

    /**
     * Identificador numérico de la textura.
     */
    private int textureId;

    /**
     * @param path Ruta al archivo .png de la textura.
     */
    public StaticTexture(String path) {
        this.PATH = path;
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
    public void bind() {
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public void init() {
        this.textureId = this.generateSprite(this.PATH);
    }

    @Override
    public String toString() {
        return String.format("StaticTexture(path=%s)", this.PATH);
    }
}
