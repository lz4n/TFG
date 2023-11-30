package utils.render.texture;

import org.lwjgl.opengl.GL20;

public final class Textures {
    public final static Texture
        SELECTOR = new StaticTexture("assets/textures/ui/selector.png"),
        WORLD_BORDER = new AnimatedTexture("assets/textures/terrain/border", 5, 5, GL20.GL_REPEAT),

        /** TERRAIN **/
        WATER = new AnimatedTexture("assets/textures/terrain/water", 5, 8),
        GRASS = new StaticTexture("assets/textures/terrain/grass.png"),
        SAND = new StaticTexture("assets/textures/terrain/sand.png"),
        STONE = new StaticTexture("assets/textures/terrain/stone.png"),
        SNOW = new StaticTexture("assets/textures/terrain/snow.png"),
        GRAVEL = new StaticTexture("assets/textures/terrain/gravel.png"),

        /** FEATURES **/
        ROCK = new StaticTexture("assets/textures/feature/rock/rock.png"),
        TULIP = new StaticTexture("assets/textures/feature/flower/tulip.png"),
        TULIP_2 = new StaticTexture("assets/textures/feature/flower/tulip2.png"),
        BLUE_ORCHID = new StaticTexture("assets/textures/feature/flower/blue_orchid.png"),
        DANDELION = new StaticTexture("assets/textures/feature/flower/dandelion.png"),
        RED_LILY = new StaticTexture("assets/textures/feature/flower/red_lily.png"),
        BUSH = new StaticTexture("assets/textures/feature/bush.png"),
        TREE1 = new StaticTexture("assets/textures/feature/tree.png"),
        TREE2 = new StaticTexture("assets/textures/feature/tree2.png"),

        /** ENTITIES **/
        DUCK = new StaticTexture("assets/textures/entity/duck.png"),

        /** PARTICLES **/
        NEGATIVE_PARTICLE = new StaticTexture("assets/textures/particle/negative.png"),
        BULLDOZER_PARTICLE = new StaticTexture("assets/textures/particle/bulldozer.png"),
        POSITIVE_PARTICLE = new StaticTexture("assets/textures/particle/positive.png"),

        /** USER INTERFACE **/
        CONTAINER = new StaticTexture("assets/textures/ui/inventory/container.png"),
        SELECTED_RIGHT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/selected_right_arrow.png"),
        UNSELECTED_RIGHT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/unselected_right_arrow.png"),
        SELECTED_LEFT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/selected_left_arrow.png"),
        UNSELECTED_LEFT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/unselected_left_arrow.png"),
        SEPARATOR_WIDGET = new StaticTexture("assets/textures/ui/inventory/separator.png"),
        SELECTED_SLOT_WIDGET = new StaticTexture("assets/textures/ui/inventory/slot/selected_slot.png"),
        UNSELECTED_SLOT_WIDGET = new StaticTexture("assets/textures/ui/inventory/slot/unselected_slot.png"),
        TEXT_WIDGET = new StaticTexture("assets/textures/ui/inventory/text_field.png");


}
