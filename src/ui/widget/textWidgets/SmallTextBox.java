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
    private final static int TEXT_SIZE = 15;

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
                    posX + 2.5f * pixelSizeInScreen,
                    posY + 2.5f * pixelSizeInScreen,
                    this.textTexture.getSize().x(),
                    this.textTexture.getSize().y());
        }
    }

    public void setText(String text, int style) {
        this.text = text;
        this.style = style;

        //Creamos la textura, y si existe una previamente se elimina para evitar fugas de memoria.
        if (this.textTexture != null) {
            this.textTexture.remove();
        }
        this.textTexture = GameFont.PIXEL.drawText(this.text, this.style, (int) (SmallTextBox.TEXT_SIZE *Container.pixelSizeInScreen));
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        //Cuando se cambia el tamaño de la ventana hay que cambiar el tamaño del texto.
        if (this.text != null) {
            this.setText(this.text, this.style);
        }
    }
}
