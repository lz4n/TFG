package ui;

import main.Main;
import ui.container.Inventory;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

@IgnoreScrollMovement
public class Tab extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(26, 12);

    public static final int TAB_SPACING = 1, WIDGET_OFFSET_Y = -10;
    public static final float TOTAL_TAB_SIZE = Tab.TAB_SPACING + Tab.BASE_BOUNDING_BOX.getWidth();

    private float width = 0;
    private boolean isSelected = false;
    private final Texture SELECTED_CONTENT, UNSELECTED_CONTENT;


    public Tab(Texture selectedContent, Texture unselectedContent) {
        super(0, 0, Tab.BASE_BOUNDING_BOX);
        this.SELECTED_CONTENT = selectedContent;
        this.UNSELECTED_CONTENT = unselectedContent;
    }

    public void updateWidth(float maxWidth) {
        this.width = Math.max(this.width, maxWidth + Inventory.WIDGET_SPACING);
    }

    public float getWidth() {
        return this.width;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public Texture getTexture() {
        return this.isSelected? Textures.SELECTED_TAB: Textures.UNSELECTED_TAB;
    }

    @Override
    public void onClickEvent() {
        if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
            inventory.unselectAllTabs();
            this.isSelected = true;
            inventory.setCurrentTab(this);
        }
        super.onClickEvent();
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        Shader.HUD.upload2f("uPosition", posX + 2f * pixelSizeInScreen, posY + 1f * pixelSizeInScreen);
        Shader.HUD.upload2f("uSize", (Tab.BASE_BOUNDING_BOX.getWidth() -4) * pixelSizeInScreen, (Tab.BASE_BOUNDING_BOX.getHeight() -3) * pixelSizeInScreen);

        ((this.isHovered() || this.isSelected)? this.SELECTED_CONTENT: this.UNSELECTED_CONTENT).draw(Shader.HUD,
                posX +2f *pixelSizeInScreen,
                posY +1f *pixelSizeInScreen,
                (Tab.BASE_BOUNDING_BOX.getWidth() -4) *pixelSizeInScreen,
                (Tab.BASE_BOUNDING_BOX.getHeight() -3) *pixelSizeInScreen
                );
    }
}
