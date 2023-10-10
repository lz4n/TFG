package ui.widget;

import utils.render.texture.Texture;

public abstract class Widget {
    private float posX, posY;

    public Widget(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public abstract float getHeight();
    public abstract float getWidth();

    public abstract Texture getTexture();
}
