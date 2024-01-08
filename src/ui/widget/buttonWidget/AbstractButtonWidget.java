package ui.widget.buttonWidget;

import org.joml.Vector2f;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;

public abstract class AbstractButtonWidget extends Widget implements CustomDrawWidget {
    private static final int CLICK_TIME = 15;

    private final Texture TEXTURE, CLICKED_TEXTURE;
    private final BoundingBox BASE_BOUNDING_BOX;
    private final Vector2f CLICK_OFFSET;
    protected byte clickTime = -1;

    public AbstractButtonWidget(float posX, float posY, Texture texture, Texture clickedTexture, Vector2f clickOffset, BoundingBox baseBoundingBox) {
        super(posX, posY, baseBoundingBox);
        this.TEXTURE = texture;
        this.CLICKED_TEXTURE = clickedTexture;
        this.CLICK_OFFSET = clickOffset;
        this.BASE_BOUNDING_BOX = baseBoundingBox.clone();
    }

    @Override
    public Texture getTexture() {
        return this.clickTime > -1 ? this.CLICKED_TEXTURE : this.TEXTURE;
    }

    public abstract Texture getIcon();

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        if (this.getIcon() != null) {
            if (this.getIcon() instanceof Graphics2dTexture graphics2dIcon) {
                this.getIcon().draw(Shader.HUD,
                        posX +((this.getBoundingBox().getWidth() -this.getIcon().getSize().x() /pixelSizeInScreen) /2f +(this.isHovered()?this.CLICK_OFFSET.x():0)) *pixelSizeInScreen,
                        posY +((this.getBoundingBox().getHeight() -this.getIcon().getSize().y() /pixelSizeInScreen) /2f +(this.isHovered()?this.CLICK_OFFSET.y():0)) *pixelSizeInScreen,
                        this.getIcon().getSize().x(),
                        this.getIcon().getSize().y()
                );
            } else {
                this.getIcon().draw(Shader.HUD,
                        posX +((this.getBoundingBox().getWidth() -this.getIcon().getSize().x()) /2f +(this.isHovered()?this.CLICK_OFFSET.x():0)) *pixelSizeInScreen,
                        posY +((this.getBoundingBox().getHeight() -this.getIcon().getSize().y()) /2f +(this.isHovered()?this.CLICK_OFFSET.y():0)) *pixelSizeInScreen,
                        this.getIcon().getSize().x() *pixelSizeInScreen,
                        this.getIcon().getSize().y() *pixelSizeInScreen
                );
            }
        }

        if (this.clickTime > -1) {
            this.clickTime--;
        }
    }

    @Override
    public void onClickEvent() {
        super.onClickEvent();
        this.clickTime = AbstractButtonWidget.CLICK_TIME;
    }
}
