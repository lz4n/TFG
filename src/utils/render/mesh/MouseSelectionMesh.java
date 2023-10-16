package utils.render.mesh;

public abstract class MouseSelectionMesh extends Mesh {
    public MouseSelectionMesh(int... attributesSize) {
        super(attributesSize);
    }

    public abstract void setVertex(float x, float y, int sizeX, int sizeY);
}
