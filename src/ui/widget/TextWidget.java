package ui.widget;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

import java.awt.*;

public class TextWidget extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(32, 16);

    private final Graphics2dTexture TEXT;

    public TextWidget(float posX, float posY, String text) {
        super(posX, posY, TextWidget.BASE_BOUNDING_BOX);
        this.TEXT = new Graphics2dTexture((int) (TextWidget.BASE_BOUNDING_BOX.getWidth() *100), (int) (TextWidget.BASE_BOUNDING_BOX.getHeight() *100));

        Graphics2D graphics2D = this.TEXT.getGraphics();
        graphics2D.setFont(new Font(Font.DIALOG, Font.BOLD, 1000));
        graphics2D.drawString(text, 0, TextWidget.BASE_BOUNDING_BOX.getHeight() *100);
        this.TEXT.convert();
    }

    @Override
    public Texture getTexture() {
        return Textures.TEXT_WIDGET;
    }

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        Shader.HUD.upload2f("uHudPosition", posX + 2.5f * pixelSizeInScreen, posY + 2.5f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", (TextWidget.BASE_BOUNDING_BOX.getWidth() -5) * pixelSizeInScreen, (TextWidget.BASE_BOUNDING_BOX.getHeight() -5) * pixelSizeInScreen);

        this.TEXT.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        this.TEXT.bind();
    }
}
