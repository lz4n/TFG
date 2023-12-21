package ui.widget.textWidgets;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;

public abstract class AbstractTextBox extends Widget implements CustomDrawWidget {

    private Graphics2dTexture text;

    public AbstractTextBox(float posX, float posY, BoundingBox baseBoundingBox, String text, int style) {
        super(posX, posY, baseBoundingBox);
        this.setText(text, style);
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        this.text.draw(Shader.HUD,
                posX + 2.5f * pixelSizeInScreen,
                posY + 2.5f * pixelSizeInScreen,
                this.text.getSize().x(),
                this.text.getSize().y());
    }

    public abstract int getTextSize();

    public void setText(String text, int style) {
        if (this.text != null) {
            this.text.remove();
        }
        this.text = GameFont.PIXEL.drawText(text, style, this.getTextSize());
    }
}
