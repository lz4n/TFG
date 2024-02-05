package world.location;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.feature.Feature;
import world.terrain.Terrain;

import java.io.Serializable;

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

    /**
     * @return Devuelve el componente Y.
     */
    public float getY() {
        return y;
    }

    /**
     * @return Si la posición está fuera del mundo.
     */
    public boolean isOutOfTheWorld() {
        return !(x >= 0 && x < Main.world.getSize() && y >= 0 && y < Main.world.getSize());
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
     * @return Convierte la posición a un vector 2i
     */
    public Vector2i toVector2i() {
        return new Vector2i((int) this.getX(), (int) this.getY());
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

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("Location(x=%f,y=%f)", this.getX(), this.getY());
    }
}
