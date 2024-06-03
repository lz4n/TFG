package world.location;

import listener.MouseListener;
import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import ui.container.Container;
import utils.render.Camera;
import utils.render.Window;
import utils.render.scene.WorldScene;
import world.feature.Feature;
import world.terrain.Terrain;

import java.io.Console;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una posición del mundo.
 */
public class Location implements Cloneable, Serializable {
    /**
     * Componentes X e Y de la posición.
     */
    private float x, y;

    /**
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return Devuelve el componente X.
     */
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return Devuelve el componente Y.
     */
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return Si la posición está fuera del mundo.
     */
    public boolean isOutOfTheWorld() {
        return !(x >= 0 && x < Main.world.getSize() && y >= 0 && y < Main.world.getSize());
    }

    /**
     * Calcula la distancia entre el punto y otro que se le pasa cómo parámetro, utilizando el Teorema de Pitágoras.
     * @param location Otro punto desde el cuál se quiere obtener la distancia.
     * @return Distancia entre ambos puntos.
     */
    public double getDistance(Location location) {
        return Math.sqrt(Math.pow(this.getX() - location.getX(), 2) + Math.pow(this.getY() - location.getY(), 2));
    }

    /**
     * Añade un valor a los componentes de la posición.
     * @param x Adición en el eje X.
     * @param y Adición en el eje Y.
     * @return <code>this</code>.
     */
    public Location add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Añade un valor total tanto al eje x como y
     * @param amount cantidad de distancia.
     * @return <code>this</code>.
     */
    public Location add(float amount) {
        return this.add(amount, amount);
    }

    public Location add(Vector2fc amount) {
        return this.add(amount.x(), amount.y());
    }


    /**
     * Multiplica los componentes por un valor.
     * @param factor Valor por el cuál se quiere multiplicar.
     * @return <code>this</code>.
     */
    public Location multiply(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    /**
     * Trunca (quita los decimales) de los componentes.
     * @return <code>this</code>.
     */
    public Location truncate() {
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        return this;
    }

    /**
     * Convierte las coordenadas in-game a coordenadas de pantalla.
     * @return Posición con las coordenadas de pantalla.
     */
    public Location getInScreenCoords() {
        return this.clone().multiply(WorldScene.SPRITE_SIZE);
    }

    /**
     * @return Convierte la posición a un vector 2f
     */
    public Vector2f toVector2f() {
        return new Vector2f(this.getX(), this.getY());
    }

    /**
     * @return Terreno en dicha posición, <code>null</code> si está fuera del mundo.
     * @see Terrain
     */
    public Terrain getTerrain() {
        return Main.world.getTerrain((int) this.getX(), (int) this.getY());
    }

    /**
     * @return Feature en dicha posición, <code>null</code> si no hay ninguna feature.
     * @see Feature
     */
    public Feature getFeature() {
        return Main.world.getFeature((int) this.getX(), (int) this.getY());
    }

    public Location randomLocation(int radius) {
        Location randomLocation;

        do {
            randomLocation = this.clone().add(
                    Main.RANDOM.nextInt(radius *2) -radius,
                    Main.RANDOM.nextInt(radius *2) -radius);
        } while (randomLocation.isOutOfTheWorld());

        return randomLocation;
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException exception) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Location location = (Location) obj;
        return (location.x == this.x) && (location.y == this.y);
    }

    @Override
    public String toString() {
        return String.format("Location(x=%f,y=%f)", this.getX(), this.getY());
    }
}
