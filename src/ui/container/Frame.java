package ui.container;

import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.Window;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

import java.awt.*;

public class Frame extends Container {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(100, 156);
    private final static int UNIT_TEXT_SIZE = 15;

    private float posX, posY, posXName;
    private final String NAME;
    private Graphics2dTexture nameTexture;

    public Frame(String name) {
        this.NAME = name;
    }

    public void addWidget(Widget widget) {
     super.widgets.add(widget);
    }

    @Override
    public void draw(Mesh mesh) {
        Textures.FRAME_CONTAINER.draw(
                Shader.HUD,
                this.posX *Container.pixelSizeInScreen,
                this.posY *Container.pixelSizeInScreen,
                Frame.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen,
                Frame.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen
        );

        this.nameTexture.draw(Shader.HUD,
                this.posXName,
                (this.posY +2.5f) *Container.pixelSizeInScreen,
                this.nameTexture.getSize().x(),
                this.nameTexture.getSize().y());

        super.widgets.forEach(widget -> {
            float widgetPosX = Container.pixelSizeInScreen *(widget.getPosX() +this.posX),
                    widgetPosY = Container.pixelSizeInScreen *(widget.getPosY() +this.posY),
                    widgetSizeX = Container.pixelSizeInScreen *widget.getBoundingBox().getWidth(),
                    widgetSizeY = Container.pixelSizeInScreen *widget.getBoundingBox().getHeight();

            widget.getTexture().draw(Shader.HUD, widgetPosX, widgetPosY, widgetSizeX, widgetSizeY);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(Container.pixelSizeInScreen, Container.pixelSizeInScreen *(widget.getPosX() +this.posX), Container.pixelSizeInScreen *(widget.getPosY() +this.posY));
            }
        });
    }

    @Override
    public boolean onMouseMoveEvent(float mouseX, float mouseY) {
        float interfaceX = mouseX /Container.pixelSizeInScreen -this.posX;
        float interfaceY = mouseY /Container.pixelSizeInScreen -this.posY;


        boolean isMouseOnAnyWidget = false;
        for (Widget widget: this.widgets) {
            if (widget.getBoundingBox().containsLocation(interfaceX, interfaceY)) {
                widget.setHovered(true);
                isMouseOnAnyWidget = true;
            } else {
                widget.setHovered(false);
            }
        }

        return isMouseOnAnyWidget || (
                interfaceX >= 0 &&
                interfaceY >= 0 &&
                interfaceX <= Frame.BASE_BOUNDING_BOX.getWidth() &&
                interfaceY<= Frame.BASE_BOUNDING_BOX.getHeight()
                );
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        super.onResizeWindowEvent(newWidth, newHeight);

        this.posX = ((newWidth /Container.pixelSizeInScreen) - Frame.BASE_BOUNDING_BOX.getWidth()) /2f;
        this.posY = 20;

        if (this.nameTexture != null) {
            this.nameTexture.remove();
        }
        this.nameTexture = GameFont.PIXEL.drawText(this.NAME, Font.PLAIN, Frame.UNIT_TEXT_SIZE *(int) Container.pixelSizeInScreen);
        this.posXName = (newWidth - this.nameTexture.getSize().x()) /2f;
    }
}
