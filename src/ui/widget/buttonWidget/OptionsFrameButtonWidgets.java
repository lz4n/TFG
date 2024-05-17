package ui.widget.buttonWidget;

import ui.container.Container;
import utils.render.GameFont;
import utils.render.texture.Graphics2dTexture;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class OptionsFrameButtonWidgets<T> extends FrameButtonWidget {
    private final static int TITLE_TEXT_SIZE = 6, VALUE_TEXT_SIZE = 4;

    private final Map<T, String> OPTIONS;
    private Iterator<Map.Entry<T, String>> iterator;
    private Map.Entry<T, String> currentEntry;
    private Consumer<T> onValueChangeEvent = null;

    /**
     * @param posX Posición en el eje X.
     * @param posY Posición en el eje Y.
     * @param text Texto del botón.
     */
    public OptionsFrameButtonWidgets(float posX, float posY, String text, Map<T, String> options, T initialValue) {
        super(posX, posY, text);

        this.OPTIONS = options;

        this.iterator = this.OPTIONS.entrySet().iterator();

        while (this.iterator.hasNext()) {
            this.currentEntry = this.iterator.next();

            if (this.currentEntry.getKey().equals(initialValue)) {
                break;
            }
        }

        this.updateTextTexture();
    }

    public void setOnValueChangeEvent(Consumer<T> onValueChangeEvent) {
        this.onValueChangeEvent = onValueChangeEvent;
    }

    private void nextOption() {
        //Reseteamos el iterador si no hay más elementos para iterar.
        if (!this.iterator.hasNext()) {
            this.iterator = this.OPTIONS.entrySet().iterator();
        }

        this.currentEntry = this.iterator.next();

        if (this.onValueChangeEvent != null) {
            this.onValueChangeEvent.accept(this.getCurrentOption());
        }

        this.updateTextTexture();
    }

    public T getCurrentOption() {
        return this.currentEntry.getKey();
    }

    private String getCurrentOptionName() {
        return this.currentEntry.getValue();
    }

    private void updateTextTexture() {
        //Recalculamos la textura del texto (si ya había una textura la borramos, para evitar una fuga de memoria).
        if (this.textTexture != null) {
            this.textTexture.remove();
        }

        this.textTexture = new Graphics2dTexture((int) (FrameButtonWidget.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen), (int) (FrameButtonWidget.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();

        // Escribimos el texto del botón
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, OptionsFrameButtonWidgets.TITLE_TEXT_SIZE * Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int titleHeight = fontMetrics.getHeight();
        graphics.drawString(this.TEXT, (width - fontMetrics.stringWidth(this.TEXT)) / 2, titleHeight);

        // Escribimos el valor del botón
        String value = this.getCurrentOptionName();
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, OptionsFrameButtonWidgets.VALUE_TEXT_SIZE * Container.pixelSizeInScreen));
        fontMetrics = graphics.getFontMetrics();
        int valueHeight = fontMetrics.getHeight();
        graphics.drawString(value, (width - fontMetrics.stringWidth(value)) / 2, height - valueHeight);

        this.textTexture.convert();
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        this.updateTextTexture();
    }

    @Override
    public void onMouseClickEvent() {
        this.nextOption();

        super.onMouseClickEvent();
    }
}
