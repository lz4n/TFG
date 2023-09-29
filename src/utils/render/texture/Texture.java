package utils.render.texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

public abstract class Texture {
    public abstract void bind();

    public static void unbind() {
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    protected final int generateSprite(String path) {
        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1), channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image;
        int textureId = GL20.glGenTextures();

        //Generamos la texetura
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);

        //Repetimos la textura en todas direcciones
        List.of(GL20.GL_TEXTURE_WRAP_S, GL20.GL_TEXTURE_WRAP_T).forEach(textureRepeatDirection -> {
            GL20.glTexParameteri(GL20.GL_TEXTURE_2D, textureRepeatDirection, GL20.GL_REPEAT);
        });

        //Cuando hagamos la textura más grande o más pequeña se pixele.
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        image = STBImage.stbi_load(path, width, height, channels, 0);
        if (image != null) {
            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, width.get(0), height.get(0), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
        } else {
            System.err.printf("No se ha podido cargar la textura '%s'.\n", path);
        }

        return textureId;
    }
}
