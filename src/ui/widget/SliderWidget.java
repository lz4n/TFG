package ui.widget;

import listener.MouseListener;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import ui.container.Container;
import ui.container.Frame;
import ui.widget.buttonWidget.AbstractButtonWidget;
import ui.widget.buttonWidget.FrameButtonWidget;
import ui.widget.buttonWidget.OptionsFrameButtonWidgets;
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
import java.util.function.Consumer;

public class SliderWidget extends Widget implements CustomDrawWidget, OnResizeWindowEvent, OnMouseDragEvent, OnMouseMoveEvent {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(84, 16);

    /**
     * Tamaño del texto por pixel in-game.
     */
    private final static int TITLE_TEXT_SIZE = 6, VALUE_TEXT_SIZE = 4;

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

    private boolean isClicked = false;

    private float sliderValue = 0;

    private Consumer<Float> onValueChangeEvent = null;

    /**
     * @param posX Posición en el eje X del botón.
     * @param posY Posición en el eje Y del botón.
     * @param text Texto que muestra el slider.
     */
    public SliderWidget(float posX, float posY, String text) {
        super(posX, posY, BASE_BOUNDING_BOX);
        this.text = text;
    }

    private void reloadTextTexture() {
        //Recalculamos la textura del texto (si ya había una textura la borramos, para evitar una fuga de memoria).
        if (this.textTexture != null) {
            this.textTexture.remove();
        }

        this.textTexture = new Graphics2dTexture((int) (SliderWidget.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen), (int) (SliderWidget.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();

        // Escribimos el texto del botón
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, SliderWidget.TITLE_TEXT_SIZE * Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int titleHeight = fontMetrics.getHeight();
        graphics.drawString(this.text, (width - fontMetrics.stringWidth(this.text)) / 2, titleHeight);

        // Escribimos el valor del botón
        String value = String.format("%.0f%%", this.sliderValue *100);
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, SliderWidget.VALUE_TEXT_SIZE * Container.pixelSizeInScreen));
        fontMetrics = graphics.getFontMetrics();
        int valueHeight = fontMetrics.getHeight();
        graphics.drawString(value, (width - fontMetrics.stringWidth(value)) / 2, height - valueHeight);

        this.textTexture.convert();
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

    public void setValue(float value) {
        this.sliderValue = value;

        if (this.onValueChangeEvent != null) {
            this.onValueChangeEvent.accept(this.sliderValue);
        }

        this.reloadTextTexture();
    }

    public void setOnValueChangeEvent(Consumer<Float> onValueChangeEvent) {
        this.onValueChangeEvent = onValueChangeEvent;
    }

    @Override
    public Texture getTexture() {
        return Textures.SLIDER_BACKGROUND;
    }

    @Override
    public void onMouseClickEvent() {
        this.isClicked = true;
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        this.reloadTextTexture();
    }

    @Override
    public void onMouseDragEvent(float mouseX, float mouseY) {
        if (this.isClicked) {
             this.setValue((Math.clamp(
                    mouseX,
                    this.getPosX(),
                    this.getPosX() +SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()
            ) -this.getPosX()) / (SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()));
        }
    }

    @Override
    public void onMouseMoveEvent() {
        if (!MouseListener.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1) && !this.isHovered()) {
            this.isClicked = false;
        }
    }
}
