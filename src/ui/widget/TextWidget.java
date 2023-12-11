package ui.widget;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utils.BoundingBox;
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

    public TextWidget(float posX, float posY, String text) {
        super(posX, posY, TextWidget.BASE_BOUNDING_BOX);
        this.setText(text);
    }

    @Override
    public Texture getTexture() {
        return Textures.TEXT_WIDGET;
    }

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        Shader.HUD.upload2f("uHudPosition", posX + 2.5f * pixelSizeInScreen, posY + 2.5f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", (TextWidget.BASE_BOUNDING_BOX.getWidth() -5) * pixelSizeInScreen, (TextWidget.BASE_BOUNDING_BOX.getHeight() -5) * pixelSizeInScreen);

        this.text.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        this.text.bind();
    }

    public void setText(String text) {
        /*this.text = new Graphics2dTexture((int) (TextWidget.BASE_BOUNDING_BOX.getWidth() *10), (int) (TextWidget.BASE_BOUNDING_BOX.getHeight() *10));

        Graphics2D graphics2D = this.text.getGraphics();
        graphics2D.setFont(new Font(Font.DIALOG, Font.BOLD, 1000));
        graphics2D.drawString(text, 0, TextWidget.BASE_BOUNDING_BOX.getHeight() *10);
        this.text.convert();*/

        try (InputStream fontStream = new DataInputStream(new FileInputStream("assets/font/arial.ttf"))) {
            BufferedImage fontImage = ImageIO.read(fontStream);
            byte[] fontData = new byte[fontImage.getWidth() * fontImage.getHeight()];
            fontImage.getRGB(0, 0, fontImage.getWidth(), fontImage.getHeight(), fontData, 0, fontImage.getWidth());

            // Generate a texture
            int textureId = 1;
            GL11.glGenTextures(1, textureId);
            GL11.glBindTexture(GL_TEXTURE_2D, textureId);

            // Load the font data into the texture
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, fontData.length, 1, 0, GL_RED, GL_UNSIGNED_BYTE, fontData);

            // Set the drawing mode
            GL11.glDrawArrays(GL_POINTS, 0, 1);

            // Set the position of the text
            GL11.glRasterPos2f(10, 10);

            // Render the text
            GL11.glDrawArrays(GL_POINTS, 0, 1);
        } catch (IOException exception) {

        }

    }
}
