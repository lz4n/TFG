package utils.render.texture;

import org.lwjgl.opengl.GL20;

public class AnimatedTexture extends Texture {
    private final String[] FRAMES;
    private int currentFrame = 0, textureId;
    private final int FPS;

    public AnimatedTexture(String path, int framesCount, int fps) {
        this.FPS = fps;
        this.FRAMES = new String[framesCount];

        for (int frame = 0; frame < framesCount; frame++) {
            this.FRAMES[frame] = String.format("%s/%s.png", path, frame);
        }
    }

    @Override
    public void unbind() {
        GL20.glDeleteTextures(this.textureId);
        super.unbind();
    }

    @Override
    public void bind(){
        this.currentFrame++;
        if (this.currentFrame / this.FPS >= this.FRAMES.length) {
            this.currentFrame = 0;
        }

        this.textureId = this.generateSprite(this.FRAMES[this.currentFrame / this.FPS]);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }
}
