package game.ui.widget;

import game.listener.MouseListener;
import game.ui.container.Container;
import game.utils.BoundingBox;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import game.ui.widget.widgetUtils.CustomDrawWidget;
import game.ui.widget.widgetUtils.OnMouseDragEvent;
import game.ui.widget.widgetUtils.OnMouseMoveEvent;
import game.ui.widget.widgetUtils.OnResizeWindowEvent;
import game.utils.render.GameFont;
import game.utils.render.Shader;
import game.utils.render.texture.Graphics2dTexture;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;

import java.awt.*;
import java.util.function.Consumer;

public class SliderWidget extends Widget implements CustomDrawWidget, OnResizeWindowEvent, OnMouseDragEvent, OnMouseMoveEvent {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(84, 16);
    private final static int TITLE_TEXT_SIZE = 6, VALUE_TEXT_SIZE = 4;
    private static final Vector2f HOVER_OFFSET = new Vector2f(0, -1);
    private static final Vector2f SLIDER_SIZE = new Vector2f(5, 16);
    private String text;
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

        this.textTexture = new Graphics2dTexture((int) (SliderWidget.BASE_BOUNDING_BOX.getWidth() * game.ui.container.Container.pixelSizeInScreen), (int) (SliderWidget.BASE_BOUNDING_BOX.getHeight() * game.ui.container.Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();

        // Escribimos el texto del botón
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, SliderWidget.TITLE_TEXT_SIZE * game.ui.container.Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int titleHeight = fontMetrics.getHeight();
        graphics.drawString(this.text, (width - fontMetrics.stringWidth(this.text)) / 2, titleHeight);

        // Escribimos el valor del botón
        String value = String.format("%.0f%%", this.sliderValue *100);
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, SliderWidget.VALUE_TEXT_SIZE * game.ui.container.Container.pixelSizeInScreen));
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
                posX +(SliderWidget.BASE_BOUNDING_BOX.getWidth() -SliderWidget.SLIDER_SIZE.x()) * Container.pixelSizeInScreen *this.sliderValue,
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

    /**
     * Establece el valor del slider y llama al evento de cambio de valor si está definido.
     *
     * @param value El nuevo valor del slider.
     */
    public void setValue(float value) {
        this.sliderValue = value;

        if (this.onValueChangeEvent != null) {
            this.onValueChangeEvent.accept(this.sliderValue);
        }

        this.reloadTextTexture();
    }

    /**
     * Establece el consumidor que será invocado cuando ocurra un cambio de valor en el slider.
     *
     * @param onValueChangeEvent El consumidor que manejará el evento de cambio de valor. Debe aceptar un argumento de tipo Float.
     */
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
