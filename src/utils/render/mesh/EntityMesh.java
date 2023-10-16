package utils.render.mesh;

import main.Main;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;

public class EntityMesh extends Mesh {
    public EntityMesh(Vector2i entitySize) {
        super(2, 2);
        this.vertexArray = new float[]{
                0, 0, 0, 1,
                WorldScene.SPRITE_SIZE * entitySize.x(), 0, 1, 1,
                WorldScene.SPRITE_SIZE * entitySize.x(), WorldScene.SPRITE_SIZE * entitySize.y(), 1, 0,
                0, WorldScene.SPRITE_SIZE * entitySize.y(), 0, 0
        };
        this.elementArray = new int[]{
                0, 1, 2,
                2, 3, 0
        };
    }
}
