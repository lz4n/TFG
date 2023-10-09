package ui;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.CustomDrawWidget;
import ui.widget.Widget;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;

import java.util.LinkedList;
import java.util.List;

public class Inventory extends Widget {
    public static final Texture CONTAINER = new StaticTexture("assets/textures/ui/inventory/container.png");

    private final float height;
    private final float width;
    private final float pixelSizeInScreen;
    private final List<Widget> WIDGETS = new LinkedList<>();

    public Inventory(float posX, float posY, float width, float height, float pixelSizeInScreen) {
        super(posX / pixelSizeInScreen, posY / pixelSizeInScreen);
        this.height = height / pixelSizeInScreen;
        this.width = width / pixelSizeInScreen;
        this.pixelSizeInScreen = pixelSizeInScreen;
    }


    public void addWidget(Widget widget) {
        WIDGETS.add(widget);
    }

    public void draw(Mesh mesh) {
        Shader.HUD.uploadInt("texture_sampler", 0);

        Shader.HUD.upload2f("hudPosition", this.pixelSizeInScreen * this.getPosX(), this.pixelSizeInScreen * this.getPosY());
        Shader.HUD.upload2f("hudSize", this.pixelSizeInScreen * this.getWidth(), this.pixelSizeInScreen * this.getHeight());

        Inventory.CONTAINER.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        Inventory.CONTAINER.bind();

        this.WIDGETS.forEach(widget -> {
            Shader.HUD.upload2f("hudPosition", this.pixelSizeInScreen * (widget.getPosX() + this.getPosX()), this.pixelSizeInScreen * (widget.getPosY() + this.getPosY()));
            Shader.HUD.upload2f("hudSize", this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());

            widget.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
            widget.getTexture().bind();

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * (widget.getPosX() + this.getPosX()), this.pixelSizeInScreen * (widget.getPosY() + this.getPosY()), this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());
            }
        });
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public Texture getTexture() {
        return Inventory.CONTAINER;
    }
}
