package ui.widget;

import main.Main;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import ui.widget.widgetUtils.RelocatableWhenResizedScreen;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

@IgnoreScrollMovement
public class ScreenIndicatorWidget extends Widget implements RelocatableWhenResizedScreen, CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(16, 29);
    private final float RELATIVE_X, RELATIVE_Y;
    private final Texture CONTENT;

    public ScreenIndicatorWidget(float relativeX, float relativeY, Texture content) {
        super(0, 0, ScreenIndicatorWidget.BASE_BOUNDING_BOX);
        this.RELATIVE_X = relativeX;
        this.RELATIVE_Y = relativeY;
        this.CONTENT = content;
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        this.CONTENT.draw(Shader.HUD,
                posX + 2.5f * pixelSizeInScreen,
                posY + 15.5f *pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getWidth() -5) *pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getHeight() -5) *pixelSizeInScreen
                );
    }

    @Override
    public Texture getTexture() {
        return this.isHovered()? Textures.HOVERED_SCREEN_INDICATOR: Textures.SCREEN_INDICATOR;
    }

    @Override
    public void onClickEvent() {
        Main.PLAYER.getInventory().removeWidget(this);
        super.onClickEvent();
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        this.setPosX(newWidth -this.RELATIVE_X);
        this.setPosY(newHeight -this.RELATIVE_Y);
    }
}
