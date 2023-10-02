package utils.render.texture;

import org.lwjgl.opengl.GL20;

public class StaticTexture extends Texture {
    private final String TEXTURE;
    private int textureId;

    public StaticTexture(String path) {
        this.TEXTURE = path;
    }

    @Override
    public void unbind() {
        GL20.glDeleteTextures(this.textureId);
        super.unbind();
    }

    @Override
    public void bind() {
        this.textureId = this.generateSprite(this.TEXTURE);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }
}
