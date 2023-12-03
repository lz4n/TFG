package ui.widget;

import utils.BoundingBox;
import utils.render.texture.Texture;

public abstract class Widget {
    private float posX, posY;
    private BoundingBox boundingBox;

    public Widget(float posX, float posY, BoundingBox baseBoundingBox) {
        this.posX = posX;
        this.posY = posY;
        this.boundingBox = baseBoundingBox.clone().move(posX, posY);
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox.clone();
    }

    public abstract Texture getTexture();
}
