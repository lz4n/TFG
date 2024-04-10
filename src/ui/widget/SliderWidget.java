package ui.widget;

import listener.MouseListener;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import ui.container.Container;
import ui.container.Frame;
import ui.widget.buttonWidget.AbstractButtonWidget;
import ui.widget.buttonWidget.FrameButtonWidget;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.OnMouseDragEvent;
import ui.widget.widgetUtils.OnMouseMoveEvent;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

import java.awt.*;

public class SliderWidget extends Widget implements CustomDrawWidget, OnResizeWindowEvent, OnMouseDragEvent, OnMouseMoveEvent {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(84, 16);

    /**
     * Tiempo que se mantiene clicado el botón, para facilitar que el usuario se de cuenta de que se ha clicado el botón.
     */
    private static final int CLICK_TIME = 15;

    /**
     * Tamaño del texto por pixel in-game.
     */
    private final static int UNIT_TEXT_SIZE = 8;

    /**
     * Desplazamiento que tiene el contenido del botón cuando se pasa el ratón por encima.
     */
    private static final Vector2f HOVER_OFFSET = new Vector2f(0, -1);

    private static final Vector2f SLIDER_SIZE = new Vector2f(5, 16);

    private String text;

    /**
     * Textura que contiene el texto del slider. Cada vez que se cambie el tamaño del botón, debe cambiar el tamaño del texto,
     * y se recalcula la textura.
     */
    private Graphics2dTexture textTexture;

    /**
     * Tiempo que va a aparecer el botón como clicado, si es negativo se entiende que el botón no está clicado.
     * @see SliderWidget#CLICK_TIME
     */
    private boolean isClicked = false;

    private float sliderValue = 0;

    /**
     * @param posX Posición en el eje X del botón.
     * @param posY Posición en el eje Y del botón.
     * @param text Texto que muestra el slider.
     */
    public SliderWidget(float posX, float posY, String text) {
        super(posX, posY, BASE_BOUNDING_BOX);
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
        this.reloadTextTexture();
    }

    private void reloadTextTexture() {
        if (this.textTexture != null) {
            this.textTexture.remove();
        }
        this.textTexture = GameFont.PIXEL.drawText(this.text, Font.PLAIN, SliderWidget.UNIT_TEXT_SIZE *(int) Container.pixelSizeInScreen);
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el slider
        Texture sliderTexture = Textures.SLIDER;
        if (this.isClicked) {
            sliderTexture = Textures.CLICKED_SLIDER;
        } else if (this.isHovered()) {
            sliderTexture = Textures.HOVERED_SLIDER;
        }

        sliderTexture.draw(
                Shader.HUD,
                posX +(SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()) *Container.pixelSizeInScreen *this.sliderValue,
                posY,
                SliderWidget.SLIDER_SIZE.x() *pixelSizeInScreen,
                SliderWidget.SLIDER_SIZE.y() *pixelSizeInScreen
        );

        //Dibujamos el texto.
        if (this.textTexture != null) {
            this.textTexture.draw(Shader.HUD,
                    posX +((this.getBoundingBox().getWidth() -this.textTexture.getSize().x() /pixelSizeInScreen) /2f +((this.isHovered() || this.isClicked)?SliderWidget.HOVER_OFFSET.x():0)) *pixelSizeInScreen,
                    posY +((this.getBoundingBox().getHeight() -this.textTexture.getSize().y() /pixelSizeInScreen) /2f +((this.isHovered() || this.isClicked)?SliderWidget.HOVER_OFFSET.y():0)) *pixelSizeInScreen,
                    this.textTexture.getSize().x(),
                    this.textTexture.getSize().y()
            );

        }
    }

    @Override
    public Texture getTexture() {
        return Textures.SLIDER_BACKGROUND;
    }

    @Override
    public void onClickEvent() {
        this.isClicked = true;
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        //Recalculamos la textura del texto (si ya había una textura la borramos, para evitar una fuga de memoria).
        if (this.textTexture != null) {
            this.textTexture.remove();
        }
        this.reloadTextTexture();
    }

    @Override
    public void onMouseDragEvent(float mouseX, float mouseY) {
        if (this.isClicked) {
             this.sliderValue = (Math.clamp(
                    mouseX,
                    this.getPosX(),
                    this.getPosX() +SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()
            ) -this.getPosX()) / (SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x());

            System.out.printf("""
                    value=%f
                    clamp=%f 
                    max=%f
                    interfacePos=%f
                    
                    """, sliderValue,
                    Math.clamp(
                            mouseX,
                            this.getPosX(),
                            this.getPosX() +SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()),
                    this.getPosX() +SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x(),
                    mouseX);
        }
    }

    @Override
    public void onMouseMoveEvent() {
        if (!MouseListener.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1) && !this.isHovered()) {
            this.isClicked = false;
        }
    }
}
