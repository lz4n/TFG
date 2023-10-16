package utils.render.mesh;

import utils.render.texture.Texture;

/**
 * <code>Mesh</code> utilizado para renderizar la <code>HUD</code>.
 * @see Mesh
 *
 * @author Izan
 */
public class HUDMesh extends Mesh {
    public HUDMesh() {
        super(2);
        this.vertexArray = new float[]{
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
        };
        this.elementArray = new int[]{
                0, 1, 2,
                2, 3, 0
        };
    }
}
