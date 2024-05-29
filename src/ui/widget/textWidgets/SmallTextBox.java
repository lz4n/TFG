package ui.widget.textWidgets;

import ui.container.Container;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

import java.awt.*;

/**
 * Una caja de texto pequeña.
 */
public class SmallTextBox extends Widget implements CustomDrawWidget, OnResizeWindowEvent {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 6);

    /**
     * Tamaño del texto por pixel in-game.
     */
    private final static int TEXT_SIZE = 4;

    /**
     * Textura que contiene el texto.
     */
    private Graphics2dTexture textTexture;

    /**
     * Texto que se está mostrando.
     */
    private String text;

    /**
     * Estilo del texto.
     */
    private int style;

    private Color color;

    /**
     * @param posX Posición en el eje X.
     * @param posY Posición en el eje Y.
     */
    public SmallTextBox(float posX, float posY) {
        super(posX, posY, SmallTextBox.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.SMALL_TEXT_BOX;
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el texto.
        if (this.textTexture != null) {
            this.textTexture.draw(Shader.HUD,
                    posX,
                    posY,
                    this.textTexture.getSize().x(),
                    this.textTexture.getSize().y());
        }
    }

    public void setText(String text, int style, Color color) {
        this.text = text;
        this.style = style;
        this.color = color;

        //Creamos la textura, y si existe una previamente se elimina para evitar fugas de memoria.
        if (this.textTexture != null) {
            this.textTexture.remove();
        }

        this.textTexture = new Graphics2dTexture((int) (SmallTextBox.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen), (int) (SmallTextBox.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();
        graphics.setColor(color);

        //Escribimos la fecha
        graphics.setFont(GameFont.PIXEL_CONTENT.getFont(Font.PLAIN, SmallTextBox.TEXT_SIZE *Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(text, (width - fontMetrics.stringWidth(text)) /2, (height +fontMetrics.getHeight() +fontMetrics.getAscent()/2 +fontMetrics.getDescent()/2) /2);

        this.textTexture.convert();
    }

    public void setText(String text, int style) {
        this.setText(text, style, Color.WHITE);
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        //Cuando se cambia el tamaño de la ventana hay que cambiar el tamaño del texto.
        if (this.text != null) {
            this.setText(this.text, this.style, this.color);
        }
    }
}
