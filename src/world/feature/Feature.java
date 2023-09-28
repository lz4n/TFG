package world.feature;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;

import java.util.Arrays;

public abstract class Feature {
    private Vector2d location;
    private Vector2i sizeInBlocks;
    private String texturePath;
    private float[] vertexArray;
    private int[] elementArray;
    private int vaoId;

    public Feature(Vector2d location, Vector2i sizeInBlocks, String texturePath) {
        this.location = location;
        this.sizeInBlocks = sizeInBlocks;
        this.texturePath = texturePath;

        float screenPosX = WorldScene.SPRITE_SIZE * (float) location.x(), screenPosY = WorldScene.SPRITE_SIZE * (float) location.y();
        int sizeX = this.sizeInBlocks.x(), sizeY = this.sizeInBlocks.y();

        this.vertexArray = new float[]{
                screenPosX + WorldScene.SPRITE_SIZE * sizeX, screenPosY, 0,                             0, 0, 0, 0,  1, 1,
                screenPosX, screenPosY + WorldScene.SPRITE_SIZE * sizeY, 0,                             0, 0, 0, 0,  0, 0,
                screenPosX +  + WorldScene.SPRITE_SIZE * sizeX, screenPosY + WorldScene.SPRITE_SIZE * sizeY, 0,    0, 0, 0, 0,  1, 0,
                screenPosX, screenPosY, 0,                                                      0, 0, 0, 0,  0, 1
        };
        this.elementArray = new int[]{
                2, 1, 0,
                0, 1, 3
        };
    }

    public Vector2d getLocation() {
        return new Vector2d(this.location);
    }

    public String getTexturePath() {
        return this.texturePath;
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public void setVaoID(int vaoId) {
        this.vaoId = vaoId;
    }

    public float[] getVertexArray() {
        return Arrays.copyOf(this.vertexArray, this.vertexArray.length);
    }

    public int[] getElementArray() {
        return Arrays.copyOf(this.elementArray, this.elementArray.length);
    }
}
