package game.utils.player.ui.widget.textWidgets;

import game.utils.player.ui.container.Container;
import game.utils.player.ui.widget.Widget;
import game.utils.BoundingBox;
import game.utils.render.GameFont;
import game.utils.render.Shader;
import game.utils.render.texture.Graphics2dTexture;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.utils.player.ui.widget.widgetUtils.CustomDrawWidget;

import java.awt.*;

/**
 * Caja de texto que muestra la hora y fecha del mundo.
 */
public class DateTimeTextBox extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 16);
    private final static int TIME_TEXT_SIZE = 10, DATE_TEXT_SIZE = 4;
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

        this.textTexture = new Graphics2dTexture((int) (DateTimeTextBox.BASE_BOUNDING_BOX.getWidth() * game.utils.player.ui.container.Container.pixelSizeInScreen), (int) (DateTimeTextBox.BASE_BOUNDING_BOX.getHeight() * game.utils.player.ui.container.Container.pixelSizeInScreen));

        int width = this.textTexture.getSize().x();
        int height = this.textTexture.getSize().y();

        Graphics2D graphics = this.textTexture.getGraphics();

        //Escribimos la hora
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, DateTimeTextBox.TIME_TEXT_SIZE * game.utils.player.ui.container.Container.pixelSizeInScreen));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(time, (width - fontMetrics.stringWidth(time)) /2, fontMetrics.getHeight() -(int) (2* game.utils.player.ui.container.Container.pixelSizeInScreen));

        //Escribimos la fecha
        graphics.setFont(GameFont.PIXEL.getFont(Font.PLAIN, DateTimeTextBox.DATE_TEXT_SIZE * game.utils.player.ui.container.Container.pixelSizeInScreen));
        fontMetrics = graphics.getFontMetrics();
        graphics.drawString(date, (width - fontMetrics.stringWidth(date)) /2, height -(int) (2* Container.pixelSizeInScreen));

        this.textTexture.convert();
    }
}
