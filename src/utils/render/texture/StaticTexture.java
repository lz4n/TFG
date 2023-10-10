package utils.render.texture;

import org.lwjgl.opengl.GL20;

/**
 * Representa una textura fija.
 *
 * @author Izan
 */
public class StaticTexture extends Texture {
    /**
     * Ruta de la textura.
     */
    private final String TEXTURE;

    /**
     * Identificador numérico de la textura.
     */
    private int textureId;

    /**
     * @param path Ruta al archivo .png de la textura.
     */
    public StaticTexture(String path) {
        this.TEXTURE = path;
    }

    @Override
    public void unbind() {
        GL20.glDeleteTextures(this.textureId);
        super.unbind();
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public void bind() {
        this.textureId = this.generateSprite(this.TEXTURE);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }
}
