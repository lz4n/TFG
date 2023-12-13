package ui.widget;

import ui.widget.widgetUtils.WidgetEvent;
import utils.BoundingBox;
import utils.render.texture.Texture;

public abstract class Widget {
    private float posX, posY;
    protected boolean isHovered = false;
    private BoundingBox boundingBox;

    private WidgetEvent onClickEvent = () -> {}, onHoverEvent = () -> {};

    public Widget(float posX, float posY, BoundingBox baseBoundingBox) {
        this.posX = posX;
        this.posY = posY;
        this.boundingBox = baseBoundingBox.clone().move(posX, posY);
    }

    public float getPosX() {
        return this.posX;
    }

    public void setPosX(float posX) {
        this.boundingBox.move(-this.posX, 0);
        this.boundingBox.move(posX, 0);
        this.posX = posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public void setPosY(float posY) {
        this.boundingBox.move(0, -this.posY).move(0, posY);
        this.posY = posY;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox.clone();
    }

    public abstract Texture getTexture();

    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public void setOnClickEvent(WidgetEvent onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    public void setOnHoverEvent(WidgetEvent onHoverEvent) {
        this.onHoverEvent = onHoverEvent;
    }

    public void onHoverEvent() {
        this.onHoverEvent.widgetEvent();
    }

    public void onClickEvent() {
        this.onClickEvent.widgetEvent();
    }
}
