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
import utils.render.texture.Textures;

import java.util.LinkedList;
import java.util.List;

/**
 * Representa un inventario y se encarga de registrar los clicks y dibujar el inventario en pantalla.
 */
public class Inventory {
    /**
     * Dimensiones del inventario, en unidades in-game, no píxeles de pantalla.
     */
    private float width, height;

    /**
     * Posición del inventario en el eje Y. Se cuenta de arriba a abajo.
     */
    private float posY;

    public float currentSliderPosX = 0;

    /**
     * Píxeles de patalla que ocupa un pixel de la interfaz in-game.
     */
    private float pixelSizeInScreen;

    /**
     * Widgets que contiene el inventario.
     */
    private final List<Widget> WIDGETS = new LinkedList<>();

    /**
     * Widsget que contiene el inventario y que se pueden clicar. También están en la lista  genérica de Widgets genéricos.
     * @see Inventory#WIDGETS
     */
    private final List<ClickableWidget> CLICKABLE_WIDGETS = new LinkedList<>();

    /**
     * Añade un Widget al inventario.
     * @param widget Widget que se quiere insertar.
     */
    public void addWidget(Widget widget) {
        this.WIDGETS.add(widget);
        if (widget instanceof ClickableWidget clickableWidget) {
            this.CLICKABLE_WIDGETS.add(clickableWidget);
        }
    }

    /**
     * Dibuja el inventario según el <code>mesh</code> que se le pase. Siempre utiliza el shader <code>HUD</code>.
     * @param mesh <code>Mesh</code> que se va a utilizar para dibujar el inventario.
     */
    public void draw(Mesh mesh) {
        Shader.HUD.uploadInt("texture_sampler", 0);

        Shader.HUD.upload2f("uHudPosition", 0, this.posY);
        Shader.HUD.upload2f("uHudSize", this.width, this.height);

        Textures.CONTAINER.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);


        this.WIDGETS.forEach(widget -> {
            Shader.HUD.upload2f("uHudPosition", this.pixelSizeInScreen * (widget.getPosX() + this.currentSliderPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY);
            Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());

            widget.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
            widget.getTexture().bind();

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * (widget.getPosX() + this.currentSliderPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY, this.pixelSizeInScreen * widget.getWidth(), this.pixelSizeInScreen * widget.getHeight());
            }
        });
    }

    /**
     * Establece cuantos píxeles de pantalla equivales a un pixel de la interfaz in-game. Actualiza las dimensiones de
     * la interfaz y su posición en el eje Y.
     * @param pixelSizeInScreen Equivalencia entre píxeles in-game y píxeles de pantalla.
     */
    public void setPixelSizeInScreen(float pixelSizeInScreen) {
        this.pixelSizeInScreen = pixelSizeInScreen;
        this.height = Window.getHeight() / 5f;
        this.width = Window.getWidth();
        this.posY = this.height * 4;
    }

    /**
     * Evento que se llama cuando se hace clic en la interfaz y se lo pasa el Widget que se haya pulsado, siempre que
     * implemente la interfaz <code>ClickableWidget</code>
     * @param mouseX Posición en el eje X del ratón, en píxeles de pantalla.
     * @param mouseY Posición en el eje Y del ratón, en píxeles de pantalla.
     */
    public void click(float mouseX, float mouseY) {
        float interfaceX = (mouseX / this.pixelSizeInScreen) - this.currentSliderPosX;
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
