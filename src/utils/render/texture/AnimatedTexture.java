package utils.render.texture;

import org.lwjgl.opengl.GL20;

public class AnimatedTexture extends Texture {
    private final String[] FRAMES;
    private int currentFrame = 0;
    private final int FPS;

    public AnimatedTexture(String path, int framesCount, int fps) {
        this.FPS = fps;
        this.FRAMES = new String[framesCount];

        for (int frame = 0; frame < framesCount; frame++) {
            this.FRAMES[frame] = String.format("%s/%s.png", path, frame);
        }
    }

    @Override
    public void bind(){
        this.currentFrame++;
        if (this.currentFrame / this.FPS >= this.FRAMES.length) {
            this.currentFrame = 0;
        }

        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.generateSprite(this.FRAMES[this.currentFrame / this.FPS]));
    }
}
