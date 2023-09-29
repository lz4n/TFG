package utils.render.mesh;

import main.Main;
import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.util.Arrays;

public abstract class Mesh {
    protected float[] vertexArray;
    protected int[] elementArray;
    private int vaoId;
    protected int elementsCount = 0;
    private final Texture TEXTURE;

    public Mesh(Texture texture) {
        this.TEXTURE = texture;
    }


    public abstract void addVertex(float x, float y, int sizeX, int sizeY);

    public float[] getVertexArray() {
        return Arrays.copyOf(this.vertexArray, this.vertexArray.length);
    }

    public int[] getElementArray() {
        return Arrays.copyOf(this.elementArray, this.elementArray.length);
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public void setVaoId(int vaoId) {
        this.vaoId = vaoId;
    }

    public final Texture getTexture() {
        return this.TEXTURE;
    }
}
