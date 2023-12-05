package ui.widget;

import org.joml.Vector4f;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

public class SlotWidget extends Widget implements CustomDrawWidget {
    public final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(16, 16);

    private final Texture CONTENT;
    private boolean isSelected = false;

    public SlotWidget(float posX, float posY, Texture content) {
        super(posX, posY, SlotWidget.BASE_BOUNDING_BOX);
        this.CONTENT = content;
    }

    @Override
    public Texture getTexture() {
        return this.isSelected? Textures.SELECTED_SLOT_WIDGET :(this.isHovered()? Textures.HOVERED_SLOT_WIDGET :Textures.UNSELECTED_SLOT_WIDGET);
    }

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        Shader.HUD.upload2f("uHudPosition", posX + 2.5f * pixelSizeInScreen, posY + 2.5f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", (SlotWidget.BASE_BOUNDING_BOX.getWidth() -5) * pixelSizeInScreen, (SlotWidget.BASE_BOUNDING_BOX.getHeight() -5) * pixelSizeInScreen);

        this.CONTENT.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        this.CONTENT.bind();
    }

    @Override
    public void onClickEvent() {
        this.isSelected = !this.isSelected;
        super.onClickEvent();
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
