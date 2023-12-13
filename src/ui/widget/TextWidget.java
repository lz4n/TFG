package ui.widget;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utils.BoundingBox;
import utils.GameFont;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;

public class TextWidget extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 16);

    private Graphics2dTexture text;

    public TextWidget(float posX, float posY, String text, int style, int size) {
        super(posX, posY, TextWidget.BASE_BOUNDING_BOX);
        this.setText(text, style, size);
    }

    @Override
    public Texture getTexture() {
        return Textures.TEXT_WIDGET;
    }

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        Shader.HUD.upload2f("uHudPosition", posX + 2.5f * pixelSizeInScreen, posY + 2.5f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", this.text.getSize().x(), this.text.getSize().y());

        this.text.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
    }

    public void setText(String text, int style, int size) {
        if (this.text != null) {
            this.text.remove();
        }
        this.text = GameFont.PIXEL.drawText(text, style, size);
    }
}
