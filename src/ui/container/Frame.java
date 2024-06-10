package ui.container;

import listener.MouseListener;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.OnMouseDragEvent;
import ui.widget.widgetUtils.WidgetEvent;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.Window;
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

    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(100, 156);
    private final static int UNIT_TEXT_SIZE = 15;
    private float posX, posY;
    private float posXName;
    private final String NAME;
    private Graphics2dTexture nameTexture;

    //** SCROLLBAR **//

    private final static Vector2f SCROLL_BAR_RAIL_POS = new Vector2f(93, 17);
    private final static Vector2f SCROLL_BAR_RAIL_SIZE = new Vector2f(4, 134);
    private final static int SLIDER_HEIGHT = 6;
    private boolean hasScrollbar = false;
    private float sliderPos = 0;
    private float maxScrollbarPos = 0;
    private BoundingBox scrollSliderBoundingBox = new BoundingBox(0, 0, 0, 0);
    private boolean isSliderHovered = true;
    private byte sliderClickTime = -1;

    //** Botón de regreso **//
    private final static BoundingBox BACK_BUTTON_BOUNDING_BOX = new BoundingBox(0, 0, 4, 7).move(8, 6);
    private boolean isBackButtonHovered = false;
    private WidgetEvent onBackClickEvent = null;

    /**
     * @param name Título del frame, que se mostrará en la parte superior del frame. No se puede modificar una vez creado.
     */
    public Frame(String name) {
        this.NAME = name;
    }

    /**
     * Evento que se lanzará cuando el usuario haga clic sobre el botón de regreso que tiene el frame. Si es nulo no se
     * mostrará el botón.
     * @param backClickEvent Evento de widget que se va a ejecutar.
     */
    public void setOnBackClickEvent(WidgetEvent backClickEvent) {
        this.onBackClickEvent = backClickEvent;
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

            float clampValue = Math.clamp(this.sliderPos +amount, -this.maxScrollbarPos, 0);
            if (clampValue != this.sliderPos) {
                this.sliderPos = clampValue;
                if (sliderClickTime != -2) {
                    this.sliderClickTime = 30;
                }
            }

            this.updateScrollbarSlider();
        }
    }

    private void updateScrollbarSlider() {
        float sliderPosX = this.posX + Frame.SCROLL_BAR_RAIL_POS.x(),
            sliderPosY = this.posY + Frame.SCROLL_BAR_RAIL_POS.y() - ((this.sliderPos /this.maxScrollbarPos) *(Frame.SCROLL_BAR_RAIL_SIZE.y() -Frame.SLIDER_HEIGHT));
        this.scrollSliderBoundingBox = new BoundingBox(
                sliderPosX,
                sliderPosY,
                sliderPosX +Frame.SCROLL_BAR_RAIL_SIZE.x(),
                sliderPosY +Frame.SLIDER_HEIGHT
        );
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

            Texture scrollbarSliderTexture = Textures.FRAME_SCROLL_BAR_SLIDER;
            if (this.sliderClickTime > -1 || sliderClickTime == -2) {
                if (this.sliderClickTime > -1) {
                    this.sliderClickTime--;
                }
                scrollbarSliderTexture = Textures.CLICKED_FRAME_SCROLL_BAR_SLIDER;
            } else if (this.isSliderHovered) {
                scrollbarSliderTexture = Textures.HOVERED_FRAME_SCROLL_BAR_SLIDER;
            }
            scrollbarSliderTexture.draw(
                    Shader.HUD,
                    this.scrollSliderBoundingBox.clone().multiply(Container.pixelSizeInScreen)
            );
        }

        //Dibujamos el botón de regreso.
        (this.isBackButtonHovered ? Textures.HOVERED_BACK_FRAME_BUTTON : Textures.BACK_FRAME_BUTTON).draw(
                Shader.HUD,
                (this.posX +Frame.BACK_BUTTON_BOUNDING_BOX.getXMin()) *Container.pixelSizeInScreen,
                (this.posY +Frame.BACK_BUTTON_BOUNDING_BOX.getYMin()) *Container.pixelSizeInScreen,
                Frame.BACK_BUTTON_BOUNDING_BOX.getWidth() * Container.pixelSizeInScreen,
                Frame.BACK_BUTTON_BOUNDING_BOX.getHeight() * Container.pixelSizeInScreen
        );

        /*
          Activamos el Scissor. El Scissor sirve para definir un rectángulo, y lo que se vaya a dibujar fuera del rectangulo
          no se dibuje. En este caso, solo se dibujarán los widgets que estén dentro del Frame, con 8 píxeles de márgen a cada lado (más el título)
         */
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        //Calculamos la esquina inferior izquierda, y el tamaño del Scissor.
        GL11.glScissor(
                (int) (this.posX *Container.pixelSizeInScreen),
                (int) (Window.getHeight() - ((Frame.BASE_BOUNDING_BOX.getHeight() +this.posY -8) *Container.pixelSizeInScreen)),
                (int) (Frame.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen),
                (int) (((Frame.BASE_BOUNDING_BOX.getHeight() -16) *Container.pixelSizeInScreen -nameTexture.getSize().y()))
        );

        //Dibujamos los widgets.
        for (Widget widget : super.widgets) {
            float widgetPosX = Container.pixelSizeInScreen * (widget.getPosX() + this.posX), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame.
                    widgetPosY = Container.pixelSizeInScreen * (widget.getPosY() + this.posY + this.sliderPos), //Las coordenadas del frame son relativas, cuyo origen está en la esquina superior izquierda del frame. Se le suma la posición del scroll.
                    widgetSizeX = Container.pixelSizeInScreen * widget.getBoundingBox().getWidth(),
                    widgetSizeY = Container.pixelSizeInScreen * widget.getBoundingBox().getHeight();

            widget.getTexture().draw(Shader.HUD, widgetPosX, widgetPosY, widgetSizeX, widgetSizeY);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(Container.pixelSizeInScreen, Container.pixelSizeInScreen * (widget.getPosX() + this.posX), Container.pixelSizeInScreen * (widget.getPosY() + this.posY + this.sliderPos));
            }
        }

        //Desactivamos el Scissor
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void onClickEvent(float mouseX, float mouseY) {
        //Control del clic en el slider.
        if (this.scrollSliderBoundingBox.clone().multiply(Container.pixelSizeInScreen).containsLocation(mouseX, mouseY)) {
            this.sliderClickTime = -2;
            return;
        }

        //Control del clic en el botón de regreso.
        if (this.isBackButtonHovered && this.onBackClickEvent != null) {
            this.onBackClickEvent.widgetEvent();
            return;
        }

        super.onClickEvent(mouseX, mouseY);
    }

    @Override
    public boolean onMouseMoveEvent(float mouseX, float mouseY) {
        //Calculamos las coordenadas del ratón en píxeles in-game, y cuyo origen de coordenadas es la esquina superior izquierda del Frame.
        float interfaceX = mouseX /Container.pixelSizeInScreen -this.posX;
        float interfaceY = mouseY /Container.pixelSizeInScreen -this.posY;


        //Miramos a ver si el ratón está sobre algún widget.
        boolean isMouseOnAnyWidget = false;
        for (Widget widget: this.widgets) {
            if (widget.getBoundingBox().containsLocation(interfaceX, interfaceY + -this.sliderPos)) {
                widget.setHovered(true);
                isMouseOnAnyWidget = true;
            } else {
                widget.setHovered(false);
            }
        }

        //Miramos si el ratón está sobre el slider.
        this.isSliderHovered = this.scrollSliderBoundingBox.clone().multiply(Container.pixelSizeInScreen).containsLocation(mouseX, mouseY);
        if (this.sliderClickTime == -2) {
            if (!this.isSliderHovered && !MouseListener.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
                this.sliderClickTime = -1;
            }
        }

        //Miramos si el ratón está sobre el botón de regreso.
        this.isBackButtonHovered = Frame.BACK_BUTTON_BOUNDING_BOX.containsLocation(interfaceX, interfaceY);

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

        for (Widget widget : super.widgets) {
            this.maxScrollbarPos = Math.max(maxScrollbarPos, widget.getPosY());
            if (this.maxScrollbarPos > ((Frame.BASE_BOUNDING_BOX.getHeight() - 16) - nameTexture.getSize().y() / Container.pixelSizeInScreen)) {
                this.hasScrollbar = true;
            }
        }
        this.maxScrollbarPos -= Frame.BASE_BOUNDING_BOX.getHeight() -16 -nameTexture.getSize().y() /Container.pixelSizeInScreen;

        this.updateScrollbarSlider();
    }

    @Override
    public void onDragEvent(float mouseX, float mouseY) {
        //Calculamos las coordenadas del ratón en pixeles in-game, cuyo origen de coordenadas es la esquina superior izquierda del Frame.
        float interfaceY = mouseY /Container.pixelSizeInScreen -this.posY;

        if (this.sliderClickTime == -2) {
            float sliderMousePosY = -Math.clamp(
                    interfaceY,
                    Frame.SCROLL_BAR_RAIL_POS.y(),
                    Frame.SCROLL_BAR_RAIL_POS.y() +  Frame.SCROLL_BAR_RAIL_SIZE.y()
            ) +Frame.SCROLL_BAR_RAIL_POS.y();

            this.sliderPos = (sliderMousePosY *this.maxScrollbarPos) /Frame.SCROLL_BAR_RAIL_SIZE.y();
            this.moveScroll(0);
            return;
        }

        for (Widget widget : this.widgets) {
            if (widget instanceof OnMouseDragEvent onMouseDragEvent) {
                onMouseDragEvent.onMouseDragEvent(mouseX / Container.pixelSizeInScreen - this.posX, mouseY / Container.pixelSizeInScreen - this.posY);
            }
        }
    }

    /**
     * Construye y gestiona la disposición vertical de widgets dentro de un Frame.
     * Permite posicionar widgets uno debajo del otro con un espaciado personalizable.
     */
    public static class FrameLayoutBuilder {
        private static final int INITIAL_POS = 24;

        private final int SPACING;
        private int posY = FrameLayoutBuilder.INITIAL_POS;

        /**
         * Crea un constructor de diseño para widgets con el espaciado especificado.
         * @param spacing Espacio vertical entre widgets.
         */
        public FrameLayoutBuilder(int spacing) {
            this.SPACING = spacing;
        }

        /**
         * Calcula la posición Y para el siguiente widget, colocándolo debajo del widget dado más el espaciado.
         * @param widget Widget del que se quiere colocar el siguiente widget debajo.
         * @return La posición Y calculada para el siguiente widget.
         */
        public int nextTo(Widget widget) {
            posY += (int) (widget.getBoundingBox().getHeight() + this.SPACING);

            return posY;
        }

        /**
         * Obtiene la posición Y actual donde se colocará el siguiente widget.
         * @return La posición Y actual.
         */
        public int nextTo() {
            return this.posY;
        }

    }
}
