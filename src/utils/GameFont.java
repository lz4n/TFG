package utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum GameFont {
    PIXEL("assets/font/pixel.ttf");

    private Font font;

    GameFont(String path) {
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
        } catch (FontFormatException e) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: formato desconocido.", Logger.LogMessageType.FATAL, this);
        } catch (IOException e) {
            Logger.sendMessage("No se ha podido cargar la fuente %s: error leyendo el archivo %s", Logger.LogMessageType.FATAL, this, path);
            e.printStackTrace(System.out);
        }
    }

    public Font getFont(int style, int size) {
        return font.deriveFont(style, size);
    }

    public static void drawText(Graphics2D graphics2D, String text, int posY, int posX) {
        
    }
}
