package world.feature;

import org.joml.Vector2d;
import org.joml.Vector2i;

public class Tree extends Feature {
    public Tree(Vector2d location) {
        super(location, new Vector2i(1, 2), "assets/textures/feature/tree.png");
    }
}
