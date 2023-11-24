package utils.render.texture;

import org.lwjgl.opengl.GL20;

import java.util.Arrays;

/**
 * Representa una textura animada que cambia su sprite según el tiempo.
 *
 * @author Izan
 */
public class AnimatedTexture extends Texture implements CacheTexture {
    /**
     * Dirección de cada una de las texturas de los frames.
     */
    private final String[] PATHS;

    /**
     * Tipo de envoltura.
     */
    private final int PARAM;

    /**
     * Identificador de cada uno de los frames de la textura.
     */
    private final int[] FRAMES;

    /**
     * índice del sprite actual de la textura.
     */
    private int currentSprite = 0;


    /**
     * Número de frames que tienen que pasar para pasar al siguiente sprite.
     */
    private final int FPS;

    /**
     * @param path Ruta al directorio que contiene todos los sprites. Cada sprite se llama con el índice (empezando por el 0) seguido de la extensión .png.
     * @param spriteCount Número de sprites que tiene la textura animada.
     * @param fps Número de frames que tiene que pasar para que pase al siguiente sprite.
     * @param param Tipo de envoltura de la textura.
     */
    public AnimatedTexture(String path, int spriteCount, int fps, int param) {
        this.PARAM = param;
        this.FPS = fps;
        this.FRAMES = new int[spriteCount];
        this.PATHS = new String[spriteCount];

        for (int sprite = 0; sprite < spriteCount; sprite++) {
            this.PATHS[sprite] = String.format("%s/%s.png", path, sprite);
        }
    }

    /**
     * Establece la envoltura en <code>GL20.GL_CLAMP_TO_EDGE</code> por defecto.
     * @param path Ruta al directorio que contiene todos los sprites. Cada sprite se llama con el índice (empezando por el 0) seguido de la extensión .png.
     * @param spriteCount Número de sprites que tiene la textura animada.
     * @param fps Número de frames que tiene que pasar para que pase al siguiente sprite.
     * @see AnimatedTexture#AnimatedTexture(String, int, int, int)
     */
    public AnimatedTexture(String path, int spriteCount, int fps) {
        this(path, spriteCount, fps, GL20.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void remove() {
        for (int textureId: this.FRAMES) {
            GL20.glDeleteTextures(textureId);
        }
    }

    @Override
    public int getTextureId() {
        return this.FRAMES[this.currentSprite / this.FPS];
    }

    @Override
    public void bind(int unit){
        this.currentSprite++;
        if (this.currentSprite / this.FPS >= this.FRAMES.length) {
            this.currentSprite = 0;
        }

        super.bind(unit);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public void init() {
        for (int sprite = 0; sprite < this.FRAMES.length; sprite++) {
            this.FRAMES[sprite] = this.generateSprite(this.PATHS[sprite], this.PARAM);
        }
    }

    @Override
    public String toString() {
        return String.format("AnimatedTexture(paths=%s)", Arrays.toString(this.PATHS));
    }
}
