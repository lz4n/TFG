package utils.render.mesh;

import utils.render.texture.Texture;

public class HUDMesh extends Mesh {
    public HUDMesh(Texture texture) {
        super(texture);
        this.vertexArray = new float[]{
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f
        };
        this.elementArray = new int[]{
                0, 1, 2,
                2, 3, 0
        };
    }
}
