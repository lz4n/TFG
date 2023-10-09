package utils.render.texture;

import org.lwjgl.opengl.GL20;

/**
 * Representa una textura animada que cambia su sprite según el tiempo.
 *
 * @author Izan
 */
public class AnimatedTexture extends Texture {
    /**
     * Ruta a cada uno de los frames de la textura.
     */
    private final String[] FRAMES;

    /**
     * índice del sprite actual de la textura.
     */
    private int currentSprite = 0;

    /**
     * Identificador numérico de la textura del frame actual.
     */
    private int currentTextureId;

    /**
     * Número de frames que tienen que pasar para pasar al siguiente sprite.
     */
    private final int FPS;

    /**
     * @param path Ruta al directorio que contiene todos los sprites. Cada sprite se llama con el índice (empezando por el 0) seguido de la extensión .png.
     * @param spriteCount Número de sprites que tiene la textura animada.
     * @param fps Número de frames que tiene que pasar para que pase al siguiente sprite.
     */
    public AnimatedTexture(String path, int spriteCount, int fps) {
        this.FPS = fps;
        this.FRAMES = new String[spriteCount];

        for (int sprite = 0; sprite < spriteCount; sprite++) {
            this.FRAMES[sprite] = String.format("%s/%s.png", path, sprite);
        }
    }

    @Override
    public void unbind() {
        GL20.glDeleteTextures(this.currentTextureId);
        super.unbind();
    }

    @Override
    public void bind(){
        this.currentSprite++;
        if (this.currentSprite / this.FPS >= this.FRAMES.length) {
            this.currentSprite = 0;
        }

        this.currentTextureId = this.generateSprite(this.FRAMES[this.currentSprite / this.FPS]);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.currentTextureId);
    }
}
