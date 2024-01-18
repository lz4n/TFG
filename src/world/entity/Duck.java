package world.entity;

import main.Main;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

/**
 * Entidad que representa un pato.
 */
public class Duck extends Entity {

    /**
     * @param location Posición donde se va a generar un pato.
     */
    public Duck(Location location) {
        super(location);
    }

    @Override
    public Texture getTexture() {
        return Textures.DUCK;
    }

    //TODO: Ia de los patos.
    @Override
    public void onTick(long deltaTime) {
        if (Main.RANDOM.nextFloat() >= 0.7) {
            this.move(new Vector2f(Main.RANDOM.nextFloat() -0.5f, Main.RANDOM.nextFloat() -0.5f));
        }
    }
}
