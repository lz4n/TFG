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

/**
 * Ventana que se muestra en mitad de la pantalla que puede contener Widgets.
 * @see Container
 * @see Widget
 */
public class Frame extends Container {

    /**
     * BoundingBox base del frame, todos los frames están en la misma posición, y tienen el mismo tamaño, por lo que la
     * BoundingBox no se clona, es común para todos los frames.
     * @see BoundingBox
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(100, 156);

    /**
     * Tamaño del texto por pixel in-game. Por ejemplo, si los pixeles in-gmae miden 2 veces el pixel en la pantalla, el texto tendrá
     * un tamaño de 30 (15 *2);
     * @see Container#pixelSizeInScreen
     */
    private final static int UNIT_TEXT_SIZE = 15;

    /**
     * Posición el frame en los ejes X e Y, en píxeles in-game.
     */
    private float posX, posY;

    /**
     * Posición del título en el eje X. El título se centra en el eje X, pero está fijo en el Y.
     */
    private float posXName;

    /**
     * Título del frame, que se muestra en la parte superior.
     */
    private final String NAME;

    /**
     * Textura del título del frame.
     */
    private Graphics2dTexture nameTexture;

    /**
     * @param name Título del frame, que se mostrará en la parte superior del frame. No se puede modificar una vez creado.
     */
    public Frame(String name) {
        this.NAME = name;
    }

    /**
     * Añade un widget al frame.
     * @param widget Widget a añadir en el frame. Las coordenadas del frame son relativas, cuyo origen está en la esquina
     * superior izquierda del frame.
     */
    public void addWidget(Widget widget) {
     super.widgets.add(widget);
    }

    @Override
    public void draw() {
        //Dibujamos el fondo del frame.
        Textures.FRAME_CONTAINER.draw(
                Shader.HUD,
                this.posX *Container.pixelSizeInScreen,
                this.posY *Container.pixelSizeInScreen,
                Frame.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen,
                Frame.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen
        );

        //Dibujamos el título del frame.
        this.nameTexture.draw(Shader.HUD,
                this.posXName,
                (this.posY +2.5f) *Container.pixelSizeInScreen, //La posición en el eje Y es siempre 2.5 píxeles in-game.
                this.nameTexture.getSize().x(),
                this.nameTexture.getSize().y());

        //Dibujamos los widgets.
        super.widgets.forEach(widget -> {
            float widgetPosX = Container.pixelSizeInScreen *(widget.getPosX() +this.posX), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame.
                    widgetPosY = Container.pixelSizeInScreen *(widget.getPosY() +this.posY), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame.
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
