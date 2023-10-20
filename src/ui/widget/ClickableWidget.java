package ui.widget;

import org.joml.Vector4f;

/**
 * Representa la funcionalidad de un Widget que se puede clickar.
 */
public interface ClickableWidget {

    /**
     * Método que se llama cada vez que se hace clic.
     */
    void click();

    /**
     * @return Devuelve un vector de 4 dimensiones de flotantes que contienen la posición en los ejes X e Y del vértice
     * superior izquierdo y la posición en los ejes X e Y del vértice inferior derecho.
     */
    Vector4f getClickArea();
}
