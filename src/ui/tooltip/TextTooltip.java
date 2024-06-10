package ui.tooltip;

import org.joml.Vector2f;
import ui.container.Container;
import utils.render.GameFont;
import utils.render.texture.Texture;

import java.awt.*;

/**
 * Clase TextTooltip que extiende Tooltip.
 * Proporciona métodos para agregar líneas de texto con diferentes estilos y tamaños al tooltip.
 * Utiliza objetos GameFont para renderizar el texto y los colores especificados.
 *
 * @see Tooltip
 */
public class TextTooltip extends Tooltip {
    /**
     * Tamaño del texto para el título en el tooltip.
     * Tamaño del texto para el contenido en el tooltip.
     */
    public static final int TITLE_TEXT_SIZE = 24, CONTENT_TEXT_SIZE = 16;
    private float textPosY = 0;

    /**
     * Agrega una línea de texto al tooltip con el estilo, tamaño y color especificados.
     *
     * @param text     Texto a agregar.
     * @param gameFont Fuente de juego utilizada para renderizar el texto.
     * @param style    Estilo de la fuente (por ejemplo, Font.PLAIN, Font.BOLD).
     * @param size     Tamaño de la fuente.
     * @param color    Color del texto.
     * @return Esta instancia de TextTooltip para permitir el encadenamiento de métodos.
     */
    public TextTooltip addLine(String text, GameFont gameFont, int style, int size, Color color) {
        Texture textTexture = gameFont.drawText(text, style, size, color);

        addTexture(new Vector2f(0, textPosY), textTexture);

        textPosY += 10;
        textPosY += textTexture.getSize().y();

        return this;
    }

    /**
     * Sobrecarga del método addLine que usa Color.WHITE por defecto para el color del texto.
     *
     * @param text     Texto a agregar.
     * @param gameFont Fuente de juego utilizada para renderizar el texto.
     * @param style    Estilo de la fuente (por ejemplo, Font.PLAIN, Font.BOLD).
     * @param size     Tamaño de la fuente.
     * @return Esta instancia de TextTooltip para permitir el encadenamiento de métodos.
     */
    public TextTooltip addLine(String text, GameFont gameFont, int style, int size) {
        return this.addLine(text, gameFont, style, size, Color.WHITE);
    }
}
