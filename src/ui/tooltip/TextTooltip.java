package ui.tooltip;

import org.joml.Vector2f;
import ui.container.Container;
import utils.render.GameFont;
import utils.render.texture.Texture;

import java.awt.*;

public class TextTooltip extends Tooltip {
    public static final int TITLE_TEXT_SIZE = 24, CONTENT_TEXT_SIZE = 16;

    private float textPosY = 0;

    public TextTooltip addLine(String text, GameFont gameFont, int style, int size, Color color) {
        Texture textTexture = gameFont.drawText(text, style, size, color);

        addTexture(new Vector2f(0, textPosY), textTexture);

        textPosY += 10;
        textPosY += textTexture.getSize().y();

        System.out.println(textPosY);

        return this;
    }

    public TextTooltip addLine(String text, GameFont gameFont, int style, int size) {
        return this.addLine(text, gameFont, style, size, Color.WHITE);
    }
}
