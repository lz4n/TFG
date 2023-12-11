package ui.widget;

import main.Main;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

@IgnoreScrollMovement
public class TabWidget extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(26, 12);

    public static final int TAB_SPACING = 1, WIDGET_OFFSET_Y = -10;
    public static final float TOTAL_TAB_SIZE = TabWidget.TAB_SPACING + TabWidget.BASE_BOUNDING_BOX.getWidth();

    private boolean isSelected = false;
    private final Texture SELECTED_CONTENT, UNSELECTED_CONTENT;


    public TabWidget(Texture selectedContent, Texture unselectedContent) {
        super(0, 0, TabWidget.BASE_BOUNDING_BOX);
        this.SELECTED_CONTENT = selectedContent;
        this.UNSELECTED_CONTENT = unselectedContent;
    }

    public void unselect() {
        this.isSelected = false;
    }

    @Override
    public Texture getTexture() {
        return this.isSelected? Textures.SELECTED_TAB: Textures.UNSELECTED_TAB;
    }

    @Override
    public void onClickEvent() {
        Main.PLAYER.getInventory().unselectAllTabs();
        this.isSelected = true;
        Main.PLAYER.getInventory().setCurrentTab(this);
        super.onClickEvent();
    }

    @Override
    public void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height) {
        Shader.HUD.upload2f("uHudPosition", posX + 2f * pixelSizeInScreen, posY + 1f * pixelSizeInScreen);
        Shader.HUD.upload2f("uHudSize", (TabWidget.BASE_BOUNDING_BOX.getWidth() -4) * pixelSizeInScreen, (TabWidget.BASE_BOUNDING_BOX.getHeight() -3) * pixelSizeInScreen);

        ((this.isHovered() || this.isSelected)? this.SELECTED_CONTENT: this.UNSELECTED_CONTENT).bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        ((this.isHovered() || this.isSelected)? this.SELECTED_CONTENT: this.UNSELECTED_CONTENT).bind();
    }
}
