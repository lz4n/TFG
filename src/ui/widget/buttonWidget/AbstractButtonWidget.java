package ui.widget.buttonWidget;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Texture;

public abstract class AbstractButtonWidget extends Widget implements CustomDrawWidget {
    private static final int CLICK_TIME = 15;

    private final Texture TEXTURE, CLICKED_TEXTURE;
    private final BoundingBox BASE_BOUNDING_BOX;
    private int clickTime = -1;

    public AbstractButtonWidget(float posX, float posY, Texture texture, Texture clickedTexture, BoundingBox baseBoundingBox) {
        super(posX, posY, baseBoundingBox);
        this.TEXTURE = texture;
        this.CLICKED_TEXTURE = clickedTexture;
        this.BASE_BOUNDING_BOX = baseBoundingBox.clone();
    }

    @Override
    public Texture getTexture() {
        return this.clickTime > -1 ? this.CLICKED_TEXTURE : this.TEXTURE;
    }

    public abstract Texture getIcon();

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        if (this.clickTime > -1) {
            this.clickTime--;
        }

        Shader.HUD.upload2f("uHudPosition", posX + 2.5f * pixelSizeInScreen, posY + 2.5f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", (this.BASE_BOUNDING_BOX.getWidth() -5) * pixelSizeInScreen, (this.BASE_BOUNDING_BOX.getHeight() -5) * pixelSizeInScreen);

        this.getIcon().bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
    }

    @Override
    public void onClickEvent() {
        super.onClickEvent();
        this.clickTime = AbstractButtonWidget.CLICK_TIME;
    }
}
