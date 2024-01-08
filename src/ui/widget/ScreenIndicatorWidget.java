package ui.widget;

import main.Main;
import ui.container.Inventory;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

@IgnoreScrollMovement
public class ScreenIndicatorWidget extends Widget implements OnResizeWindowEvent, CustomDrawWidget {
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
        if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
            inventory.removeWidget(this);
        }
        super.onClickEvent();
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        this.setPosX(newWidth -this.RELATIVE_X);
        this.setPosY(newHeight -this.RELATIVE_Y);
    }
}
