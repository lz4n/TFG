package ui;

import org.joml.Vector4f;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.ClickableWidget;
import ui.widget.CustomDrawWidget;
import ui.widget.Widget;
import utils.render.Shader;
import utils.render.Window;
import utils.render.mesh.Mesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;

import java.util.LinkedList;
import java.util.List;

public class Inventory {
    public static final Texture CONTAINER = new StaticTexture("assets/textures/ui/inventory/container.png");

    private float width, height, posY;
    private float pixelSizeInScreen;
    private final List<Widget> WIDGETS = new LinkedList<>();
    private final List<ClickableWidget> CLICKABLE_WIDGETS = new LinkedList<>();

    public void addWidget(Widget widget) {
        this.WIDGETS.add(widget);
        if (widget instanceof ClickableWidget clickableWidget) {
            this.CLICKABLE_WIDGETS.add(clickableWidget);
        }
    }

    public void draw(Mesh mesh) {
        Shader.HUD.uploadInt("texture_sampler", 0);

        Shader.HUD.upload2f("hudPosition", 0, this.posY);
        Shader.HUD.upload2f("hudSize", this.width, this.height);

        Inventory.CONTAINER.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);
        Inventory.CONTAINER.bind();

        this.WIDGETS.forEach(widget -> {
            Shader.HUD.upload2f("hudPosition", this.pixelSizeInScreen * widget.getPosX(), this.pixelSizeInScreen * widget.getPosY() + this.posY);
            Shader.HUD.upload2f("hudSize", this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());

            widget.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
            widget.getTexture().bind();

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * widget.getPosX(), this.pixelSizeInScreen * widget.getPosY() + this.posY, this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());
            }
        });
    }

    public void setPixelSizeInScreen(float pixelSizeInScreen) {
        this.pixelSizeInScreen = pixelSizeInScreen;
        this.height = Window.getHeight() / 5f;
        this.width = Window.getWidth();
        this.posY = this.height * 4;
    }

    public void click(float mouseX, float mouseY) {
        float interfaceX = mouseX / this.pixelSizeInScreen;
        float interfaceY = (mouseY - this.posY) / this.pixelSizeInScreen;

        this.CLICKABLE_WIDGETS.forEach(clickableWidget -> {
            Vector4f clickableArea = clickableWidget.getClickArea();

            if (
                    interfaceX >= clickableArea.x &&
                    interfaceX <= clickableArea.z &&
                    interfaceY >= clickableArea.y &&
                    interfaceY <= clickableArea.w
            ) {
                clickableWidget.click();
            }
        });
    }
}
