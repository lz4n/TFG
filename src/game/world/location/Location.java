package game.world.location;

import game.Main;
import game.utils.render.Window;
import game.utils.render.scene.WorldScene;
import game.world.feature.Feature;
import game.world.terrain.Terrain;
import org.joml.*;

import java.io.Serializable;
import java.lang.Math;
import java.util.Objects;

/**
 * Representa una posición del mundo.
 */
public class Location implements Cloneable, Serializable {
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
     * Establece el componente X de la posición.
     *
     * @param x Nuevo valor para el componente X.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return Devuelve el componente Y.
     */
    public float getY() {
        return y;
    }

    /**
     * Establece el componente Y de la posición.
     *
     * @param y Nuevo valor para el componente Y.
     */
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
     * @return <code>this</code> para permitir encadenar métodos.
     */
    public Location add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Añade un valor total tanto al eje x como y
     * @param amount cantidad de distancia.
     * @return <code>this</code> para permitir encadenar métodos.
     */
    public Location add(float amount) {
        return this.add(amount, amount);
    }

    /**
     * Añade un vector 2D a la posición.
     *
     * @param amount El vector 2D que se quiere añadir.
     * @return <code>this</code> para permitir encadenar métodos.
     */
    public Location add(Vector2fc amount) {
        return this.add(amount.x(), amount.y());
    }


    /**
     * Multiplica los componentes por un valor.
     * @param factor Valor por el cuál se quiere multiplicar.
     * @return <code>this</code> para permitir encadenar métodos.
     */
    public Location multiply(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    /**
     * Trunca (quita los decimales) de los componentes.
     * @return <code>this</code> para permitir encadenar métodos.
     */
    public Location truncate() {
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        return this;
    }

    /**
     * Determina el costo de camino para el pathfinding.
     *
     * @return El costo de camino.
     */
    public int getPathCost() {
        if (this.isOutOfTheWorld()) {
            return 5;
        }

        return this.getTerrain().getPathCost() + (this.getFeature() != null ? 5 : 0);
    }

    /**
     * Convierte las coordenadas in-game a coordenadas de pantalla.
     * @return Posición con las coordenadas de pantalla.
     */
    public Location getInScreenCoords() {
        return this.clone().add(0, 1).multiply(WorldScene.SPRITE_SIZE);
    }

    /**
     * Convierte las coordenadas in-game a coordenadas de pantalla relativas a la cámara.
     *
     * @return Una nueva instancia de Location con las coordenadas de pantalla relativas a la cámara.
     */
    public Location getInScreenCoordsRelative() {
        Vector4f inGamePos = new Vector4f(this.getX() * WorldScene.SPRITE_SIZE,
                this.getY() * WorldScene.SPRITE_SIZE,
                0f,
                1f);

        Vector4f clipSpacePos = new Vector4f();
        Main.player.getCamera().getProjectionMatrix().mul(Main.player.getCamera().getViewMatrix(), new Matrix4f()).transform(inGamePos, clipSpacePos);

        clipSpacePos.div(clipSpacePos.w);

        float screenX = ((clipSpacePos.x + 1) / 2.0f) * Window.getWidth();
        float screenY = ((1 - clipSpacePos.y) / 2.0f) * Window.getHeight();

        return new Location(screenX, screenY);
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

    /**
     * Genera una ubicación aleatoria dentro de un radio dado alrededor de esta ubicación.
     *
     * @param radius El radio dentro del cuál se quiere generar una ubicación aleatoria.
     * @return Una nueva instancia de Location con la ubicación aleatoria.
     */
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
