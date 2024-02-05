package utils;

import org.joml.Vector2f;
import org.joml.Vector4f;
import world.location.Location;

/**
 * Representa un área rectangular 2D imaginaria definida por dos puntos.
 */
public class BoundingBox implements Cloneable {

    /**
     * Posiciones de las esquinas.
     */
    private float xMin = 0, yMin = 0, xMax, yMax;

    /**
     * Crea una bounding box a partir de una posición de origen y un vector que representa el tamaño.
     * @param origin Posición de origen.
     * @param size Vector que representa el tamaño de la BoundingBox.
     */
    public BoundingBox(Location origin, Vector2f size) {
        this(origin.getX(), origin.getY(), origin.getX() +size.x(), origin.getY() +size.y());
    }

    /**
     * Crea una bounding box segun el ancho y el alto, con origen en las coordendas 0, 0.
     * @param width Ancho de la BoundingBox.
     * @param height Alto de la BoundingBox.
     */
    public BoundingBox(float width, float height) {
        this.xMax = width;
        this.yMax = height;
    }

    /**
     * Crea una bounding box a partir de las cuatro esquinas.
     * @param x1 Posición en el eje X de una esquina.
     * @param y1 Posición en el eje Y de una esquina.
     * @param x2 Posición en el eje X de la otra esquina.
     * @param y2 Posición en el eje Y de la otra esquina.
     */
    public BoundingBox(float x1, float y1, float x2, float y2) {
        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.yMin = Math.min(y1, y2);
        this.yMax = Math.max(y1, y2);
    }

    /**
     * Mueve la bounding box.
     * @param movementX Desplazamiento en el eje X.
     * @param movementY Desplazamiento en el eje Y.
     * @return La propia BoundingBox.
     */
    public BoundingBox move(float movementX, float movementY) {
        this.xMin += movementX;
        this.xMax += movementX;
        this.yMin += movementY;
        this.yMax += movementY;
        return this;
    }

    /**
     * Detecta si la BoundigBox está en contacto con otra.
     * @param boundingBox BoundigBox que queremos saber si está en contacto con la nuestra.
     * @return Si están en contacto o no.
     */
    public boolean collidesWith(BoundingBox boundingBox) {
        return this.xMax <= boundingBox.xMin &&
                this.xMin >= boundingBox.xMax &&
                this.yMax <= boundingBox.yMin &&
                this.yMin >= boundingBox.yMax;
    }

    /**
     * Mira si la BoundingBox contiene una posición.
     * @param location Posición de la cuál se quiere mirar si está dentro de la BoundingBox.
     * @return Si la posición está en la BoundingBox.
     */
    public boolean containsLocation(Location location) {
        return containsLocation(location.getX(), location.getY());
    }

    /**
     * Mira si la BoundingBox contiene una posición.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @return Si la posición está en la BoundingBox.
     */
    public boolean containsLocation(float x, float y) {
        return x >= this.xMin &&
                x <= this.xMax &&
                y >= this.yMin &&
                y <= this.yMax;
    }

    /**
     * @return Vector con el tamaño de la BoundingBox.
     */
    public Vector2f getSize() {
        return new Vector2f(this.getWidth(), this.getHeight());
    }

    /**
     * @return Ancho de la BoundingBox.
     */
    public float getWidth() {
        return this.xMax - this.xMin;
    }

    /**
     * @return Alto de la BoundingBox.
     */
    public float getHeight() {
        return this.yMax - this.yMin;
    }

    /**
     * @return Vector de cuatro dimensiones con los componentes de las 2 esquinas.
     */
    public Vector4f getComponents() {
        return new Vector4f(this.xMin, this.yMin, this.xMax, this.yMax);
    }

    @Override
    public BoundingBox clone() {
        try {
            return (BoundingBox) super.clone();
        } catch (CloneNotSupportedException exception) {
            Logger.sendMessage("Error de clonación: %s.", Logger.LogMessageType.FATAL, exception.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "xMin=" + xMin +
                ", yMin=" + yMin +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                '}';
    }
}
