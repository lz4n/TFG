package utils.render;

import utils.Logger;
import utils.render.texture.Graphics2dTexture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Fuentes de los textos del juego.
 */
public enum GameFont {
    PIXEL("assets/fonts/slkscr.ttf"),
    DEBUG("assets/fonts/arial.ttf");

    /**
     * Fuente awt de la fuente.
     * @see Font
     */
    private Font font;

    /**
     * @param path Ruta al archivo <code>.ttf</code> de la fuente.
     */
    GameFont(String path) {
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
        } catch (FontFormatException exception) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: formato desconocido.", Logger.LogMessageType.FATAL, this);
        } catch (IOException exception) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: error leyendo el archivo %s.", Logger.LogMessageType.FATAL, this, path);
        }
    }

    /**
     * Genera una fuente con un estilo y tamaño determinados.
     * @param style Estilo de la fuente: negrita, cursiva, etc...
     * @param size Tamaño del texto de la fuente.
     * @return Fuente awt con el estilo y tamaño que se han pasado como parámetro.
     */
    public Font getFont(int style, float size) {
        return this.font.deriveFont(style, size);
    }

    /**
     * Genera una textura con el texto que se le pasa como parámetro.
     * @param text Texto que se va a generar.
     * @param style Estilo del texto.
     * @param size Tamaño del texto.
     * @return Textura con el texto.
     */
    public Graphics2dTexture drawText(String text, int style, int size) {
        if (this.font != null) {
            int width = 1, height = 1;
            //Generamos una textura auxiliar para conseguir las métricas de la fuente.
            Graphics2D graphics2D = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
            FontMetrics fontMetrics = graphics2D.getFontMetrics(this.font.deriveFont(style, size));

            //Calculamos el tamaño del texto.
            for (String line : text.split("\n")) {
                width = Math.max(fontMetrics.stringWidth(line), width);
                height += fontMetrics.getHeight();
            }

            //Generamos la textura final.
            Graphics2dTexture texture = new Graphics2dTexture(width, height);
            graphics2D = texture.getGraphics();
            graphics2D.setFont(this.font.deriveFont(style, size));
            height = fontMetrics.getAscent();

            //Dividimos el texto en lineas y lo dibujamos..
            for (String line : text.split("\n")) {
                graphics2D.drawString(line, 0, height);
                height += fontMetrics.getHeight();
            }

            //Pasamos los gráficos 2D a la textura.
            graphics2D.dispose();
            texture.convert();

            return texture;
        }
        return new Graphics2dTexture(1, 1);
    }
}
