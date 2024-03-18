package ui.container;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.Window;
import utils.render.texture.Graphics2dTexture;
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
     * Posición donde se coloca el rail de la scrollbar, y su tamaño. En coordenadas in-game relativas, con origen en la
     * esquina superior izquierda.
     */
    private final static Vector2f SCROLL_BAR_RAIL_POS = new Vector2f(93, 17);
    private final static Vector2f SCROLL_BAR_RAIL_SIZE = new Vector2f(4, 134);

    /**
     * Altura del slider de la scrollbar.
     */
    private final static int SLIDER_HEIGHT = 6;

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

    private boolean hasScrollbar = false;
    private float scrollbarPos = 0;
    private float maxScrollbarPos = 0;

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

    /**
     * Mueve la scrollbar siempre que sea posible.
     * @param amount Cantidad de movimiento de la scrollbar.
     */
    public void moveScroll(float amount) {
        if (this.hasScrollbar()) {
            float originalScrollbarPos = this.scrollbarPos;

            this.scrollbarPos += amount;
            System.out.println(this.scrollbarPos);
            if (this.scrollbarPos < -this.maxScrollbarPos || this.scrollbarPos >= 0) {
                this.scrollbarPos = originalScrollbarPos;
            }
        }
    }

    /**
     * @return Devuelve si el Frame tiene scrollbar o no.
     */
    public boolean hasScrollbar() {
        return this.hasScrollbar;
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

        //Dibujamos el rail y el slider de la scrollbar.
        if (this.hasScrollbar()) {
            Textures.FRAME_SCROLL_BAR_RAIL.draw(
                    Shader.HUD,
                    (this.posX + Frame.SCROLL_BAR_RAIL_POS.x()) * Container.pixelSizeInScreen,
                    (this.posY + Frame.SCROLL_BAR_RAIL_POS.y()) * Container.pixelSizeInScreen,
                    Frame.SCROLL_BAR_RAIL_SIZE.x() * Container.pixelSizeInScreen,
                    Frame.SCROLL_BAR_RAIL_SIZE.y() * Container.pixelSizeInScreen
            );

            Textures.FRAME_SCROLL_BAR_SLIDER.draw(
                    Shader.HUD,
                    (this.posX + Frame.SCROLL_BAR_RAIL_POS.x()) * Container.pixelSizeInScreen,
                    (this.posY + Frame.SCROLL_BAR_RAIL_POS.y() - ((this.scrollbarPos /this.maxScrollbarPos) *(Frame.SCROLL_BAR_RAIL_SIZE.y() -Frame.SLIDER_HEIGHT))) * Container.pixelSizeInScreen,
                    Frame.SCROLL_BAR_RAIL_SIZE.x() * Container.pixelSizeInScreen,
                    Frame.SLIDER_HEIGHT * Container.pixelSizeInScreen
            );
        }

        /*
          Activamos el Scissor. El Scissor sirve para definir un rectángulo, y lo que se vaya a dibujar fuera del rectangulo
          no se dibuje. En este caso, solo se dibujarán los widgets que estén dentro del Frame, con 8 pixeles de márgen a cada lado (más el titulo)
         */
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        //Obtenemos la escala respecto al tamaño inicial de la ventana, ya que al scissor no le llegan los cambios en las dimensiones de la ventana.
        float scaleX = (float) Window.getWidth() /Window.getInitialWidth(),
                scaleY = (float) Window.getHeight() /Window.getInitialHeight();

        //Calculamos la esquina inferior izquierda, y el tamaño del Scissor.
        GL11.glScissor(
                (int) (this.posX *scaleX *Container.pixelSizeInScreen),
                (int) (Window.getHeight() - ((Frame.BASE_BOUNDING_BOX.getHeight() +this.posY -8) *Container.pixelSizeInScreen) *scaleY),
                (int) (Frame.BASE_BOUNDING_BOX.getWidth() *scaleX *Container.pixelSizeInScreen),
                (int) (((Frame.BASE_BOUNDING_BOX.getHeight() -16) *Container.pixelSizeInScreen -nameTexture.getSize().y()) *scaleY)
        );

        //Dibujamos los widgets.
        super.widgets.forEach(widget -> {
            float widgetPosX = Container.pixelSizeInScreen *(widget.getPosX() +this.posX), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame.
                    widgetPosY = Container.pixelSizeInScreen *(widget.getPosY() +this.posY +this.scrollbarPos), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame. Se le suma la posición del scroll.
                    widgetSizeX = Container.pixelSizeInScreen *widget.getBoundingBox().getWidth(),
                    widgetSizeY = Container.pixelSizeInScreen *widget.getBoundingBox().getHeight();

            widget.getTexture().draw(Shader.HUD, widgetPosX, widgetPosY, widgetSizeX, widgetSizeY);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(Container.pixelSizeInScreen, Container.pixelSizeInScreen *(widget.getPosX() +this.posX), Container.pixelSizeInScreen *(widget.getPosY() +this.posY +this.scrollbarPos));
            }
        });

        //Desactivamos el Scissor
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean onMouseMoveEvent(float mouseX, float mouseY) {
        //Calculamos las coordenadas del ratón en pixeles in-game, y cuyo origen de coordenadas es la esquina superior izquierda del Frame.
        float interfaceX = mouseX /Container.pixelSizeInScreen -this.posX;
        float interfaceY = mouseY /Container.pixelSizeInScreen -this.posY;


        //Miramos a ver si el ratón está sobre algún widget.
        boolean isMouseOnAnyWidget = false;
        for (Widget widget: this.widgets) {
            if (widget.getBoundingBox().containsLocation(interfaceX, interfaceY)) {
                widget.setHovered(true);
                isMouseOnAnyWidget = true;
            } else {
                widget.setHovered(false);
            }
        }

        //Si no está sobre algún widget miramos que el ratón esté dentro del Frame.
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

        //Recalculamos la textura del título.
        this.posX = ((newWidth /Container.pixelSizeInScreen) - Frame.BASE_BOUNDING_BOX.getWidth()) /2f;
        this.posY = 20;

        if (this.nameTexture != null) {
            this.nameTexture.remove();
        }
        this.nameTexture = GameFont.PIXEL.drawText(this.NAME, Font.PLAIN, Frame.UNIT_TEXT_SIZE *(int) Container.pixelSizeInScreen);
        this.posXName = (newWidth - this.nameTexture.getSize().x()) /2f;

        super.widgets.forEach(widget -> {
            this.maxScrollbarPos = Math.max(maxScrollbarPos, widget.getPosY());
            if (this.maxScrollbarPos > ((Frame.BASE_BOUNDING_BOX.getHeight() -16) -nameTexture.getSize().y() /Container.pixelSizeInScreen)) {
                this.hasScrollbar = true;
            }
        });
    }
}
