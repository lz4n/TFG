package utils.render.texture;

import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class Graphics2dTexture extends Texture {
    private final BufferedImage BUFFERED_IMAGE;
    private final Graphics2D GRAPHICS;
    private int textureId;

    public Graphics2dTexture(int width, int height) {
        this.BUFFERED_IMAGE = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.GRAPHICS = this.BUFFERED_IMAGE.createGraphics();
    }

    public Graphics2D getGraphics() {
        return this.GRAPHICS;
    }

    public int convert() {
        this.GRAPHICS.dispose();

        //Generamos la texetura
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
