package utils.render;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.Logger;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum GameFont {
    PIXEL("assets/fonts/slkscr.ttf"),
    DEBUG("assets/fonts/arial.ttf");

    private Font font;

    GameFont(String path) {
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
        } catch (FontFormatException exception) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: formato desconocido.", Logger.LogMessageType.FATAL, this);
        } catch (IOException exception) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: error leyendo el archivo %s.", Logger.LogMessageType.FATAL, this, path);
        }
    }

    public Font getFont(int style, float size) {
        return this.font.deriveFont(style, size);
    }

    public Graphics2dTexture drawText(String text, int style, int size) {
        if (this.font != null) {
            int width = 1, height = 1;
            Graphics2D graphics2D = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
            FontMetrics fontMetrics = graphics2D.getFontMetrics(this.font.deriveFont(style, size));

            for (String line : text.split("\n")) {
                width = Math.max(fontMetrics.stringWidth(line), width);
                height += fontMetrics.getHeight();
            }

            Graphics2dTexture texture = new Graphics2dTexture(width, height);
            graphics2D = texture.getGraphics();
            graphics2D.setFont(this.font.deriveFont(style, size));
            height = fontMetrics.getAscent();

            for (String line : text.split("\n")) {
                graphics2D.drawString(line, 0, height);
                height += fontMetrics.getHeight();
            }

            graphics2D.dispose();
            texture.convert();

            return texture;
        }
        return new Graphics2dTexture(1, 1);
    }
}
