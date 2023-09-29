package utils.render.texture;

import org.lwjgl.opengl.GL20;

public class StaticTexture extends Texture {
    private final String TEXTURE;

    public StaticTexture(String path) {
        this.TEXTURE = path;
    }

    @Override
    public void bind() {
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.generateSprite(this.TEXTURE));
    }
}
