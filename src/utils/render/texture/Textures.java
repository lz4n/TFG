package utils.render.texture;

import org.lwjgl.opengl.GL20;

public enum Textures {
    SELECTOR(new StaticTexture("assets/textures/ui/selector.png")),
    WORLD_BORDER(new AnimatedTexture("assets/textures/terrain/border", 5, 5, GL20.GL_REPEAT)),

    /** TERRAIN **/
    WATER(new AnimatedTexture("assets/textures/terrain/water", 5, 8)),
    GRASS(new StaticTexture("assets/textures/terrain/grass.png")),
    SAND(new StaticTexture("assets/textures/terrain/sand.png")),
    STONE(new StaticTexture("assets/textures/terrain/stone.png")),
    SNOW(new StaticTexture("assets/textures/terrain/snow.png")),
    GRAVEL(new StaticTexture("assets/textures/terrain/gravel.png")),

    /** PARTICLES **/
    NEGATIVE_PARTICLE(new StaticTexture("assets/textures/particle/negative.png")),

    /** USER INTERFACE **/
    CONTAINER(new StaticTexture("assets/textures/ui/inventory/container.png")),



    private final Texture TEXTURE;

    Textures(Texture texture) {
        this.TEXTURE = texture;
    }

    public Texture getTexture() {
        return this.TEXTURE;
    }
}
