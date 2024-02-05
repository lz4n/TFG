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
 * Caja de texto que muestra la hora y fecha del mundo.
 */
public class DateTimeTextBox extends Widget implements CustomDrawWidget {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 16);

    /**
     * Tamaño del texto de la hora y fecha por pixel in-game.
     */
    private final static int TIME_TEXT_SIZE = 10, DATE_TEXT_SIZE = 4;

    /**
     * Textura que contiene ambos textos.
     */
    private Graphics2dTexture textTexture;

    /**
     * @param posX Posición en el eje X.
     * @param posY Posición en el eje Y.
     */
    public DateTimeTextBox(float posX, float posY) {
        super(posX, posY, DateTimeTextBox.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.TEXT_BOX;
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el texto.
        this.textTexture.draw(Shader.HUD,
                posX,
                posY,
                this.textTexture.getSize().x(),
                this.textTexture.getSize().y());
    }

    /**
     * Borra la textura anterior y crea una nueva textura con la hora y fecha que se pasa como parámetro.
     * @param time Hora que se va a mostrar.
     * @param date Fecha que se va a mostrar
     */
    public void updateDateTime(String time, String date) {
        if (this.textTexture != null) {
            this.textTexture.remove();
        }

        this.textTexture = new Graphics2dTexture((int) (DateTimeTextBox.BASE_BOUNDING_BOX.getWidth() *Container.pixelSizeInScreen), (int) (DateTimeTextBox.BASE_BOUNDING_BOX.getHeight() *Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();

        //Escribimos la hora
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, DateTimeTextBox.TIME_TEXT_SIZE *Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(time, (width - fontMetrics.stringWidth(time)) /2, fontMetrics.getHeight() -(int) (2* Container.pixelSizeInScreen));

        //Escribimos la fecha
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, DateTimeTextBox.DATE_TEXT_SIZE *Container.pixelSizeInScreen));
        fontMetrics = graphics.getFontMetrics();
        graphics.drawString(date, (width - fontMetrics.stringWidth(date)) /2, height -(int) (2* Container.pixelSizeInScreen));

        this.textTexture.convert();
    }
}
