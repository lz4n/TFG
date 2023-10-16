package ui.widget;

import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;

public class SeparatorWidget extends Widget {
    private static final Texture TEXTURE = new StaticTexture("assets/textures/ui/inventory/separator.png");
    public SeparatorWidget(float posX, float posY) {
        super(posX, posY);
    }

    @Override
    public float getHeight() {
        return 41;
    }

    @Override
    public float getWidth() {
        return 2;
    }

    @Override
    public Texture getTexture() {
        return SeparatorWidget.TEXTURE;
    }
}
