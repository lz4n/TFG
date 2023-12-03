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

    private float currentSliderPosX = 0;

    /**
     * Píxeles de patalla que ocupa un pixel de la interfaz in-game.
     */
    private float pixelSizeInScreen;

    private boolean isShowingLeftArrow, isShowingRightArrow;
    private int clickTimeLeftArrow = -1, clickTimeRightArrow = -1;

    /**
     * Widgets que contiene el inventario.
     */
    private final List<Widget> WIDGETS = new LinkedList<>();

    /**
     * Widsget que contiene el inventario y que se pueden clicar. También están en la lista  genérica de Widgets genéricos.
     * @see Inventory#WIDGETS
     */
    private final List<ClickableWidget> CLICKABLE_WIDGETS = new LinkedList<>();

    public Inventory() {
        this.updateShowArrows();
    }

    /**
     * Añade un Widget al inventario.
     * @param widget Widget que se quiere insertar.
     */
    public void addWidget(Widget widget) {
        if (this.width + widget.getBoundingBox().getWidth() +4 > this.width) {
            this.width += widget.getBoundingBox().getWidth() +4;
        }

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
        Shader.HUD.upload2f("uHudPosition", 0, this.posY);
        Shader.HUD.upload2f("uHudSize", Window.getWidth(), this.height);

        Textures.CONTAINER.bind();
        ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);


        this.WIDGETS.forEach(widget -> {
            Shader.HUD.upload2f("uHudPosition", this.pixelSizeInScreen * (widget.getPosX() + this.currentSliderPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY);
            Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());

            widget.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * (widget.getPosX() + this.currentSliderPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY, this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());
            }
        });

        if (this.isShowingLeftArrow) {
            Shader.HUD.upload2f("uHudPosition", 0, this.posY);
            Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * 7, this.height);

            (this.clickTimeLeftArrow>-1?Textures.SELECTED_LEFT_ARROW:Textures.UNSELECTED_LEFT_ARROW).bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);

            if (this.clickTimeLeftArrow > -1) {
                this.clickTimeLeftArrow--;
            }
        }

        if (this.isShowingRightArrow) {
            Shader.HUD.upload2f("uHudPosition", Window.getWidth() - this.pixelSizeInScreen *7, this.posY);
            Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * 7, this.height);

            (this.clickTimeRightArrow>-1?Textures.SELECTED_RIGHT_ARROW:Textures.UNSELECTED_RIGHT_ARROW).bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
            
            if (this.clickTimeRightArrow > -1) {
                this.clickTimeRightArrow--;
            }
        }
    }

    /**
     * Establece cuantos píxeles de pantalla equivales a un pixel de la interfaz in-game. Actualiza las dimensiones de
     * la interfaz y su posición en el eje Y.
     * @param pixelSizeInScreen Equivalencia entre píxeles in-game y píxeles de pantalla.
     */
    public void setPixelSizeInScreen(float pixelSizeInScreen) {
        this.pixelSizeInScreen = pixelSizeInScreen;
        this.height = Window.getHeight() / 5f;
        this.posY = this.height * 4;
        this.updateShowArrows();
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

        if (this.isShowingLeftArrow &&
                interfaceX + this.currentSliderPosX <= 7 &&
                interfaceY <= this.height
        ) {
            this.moveSlider(.7f);
            return;
        }

        if (this.isShowingRightArrow &&
                interfaceX + this.currentSliderPosX >= Window.getWidth() /this.pixelSizeInScreen -7 &&
                interfaceY <= this.height
        ) {
            this.moveSlider(-.7f);
            return;
        }

        this.CLICKABLE_WIDGETS.forEach(clickableWidget -> {
            if (clickableWidget.getBoundingBox().containsLocation(interfaceX, interfaceY)) {
                clickableWidget.click();
                return;
            }
        });
    }

    public boolean moveSlider(float amount) {
        float originalSliderPos = this.currentSliderPosX;

        this.currentSliderPosX += amount;
        if (this.currentSliderPosX > this.getMaxLeftSliderPos() || this.currentSliderPosX <= this.getMaxRightSliderPos()) {
            this.currentSliderPosX = originalSliderPos;
            return false;
        }

        if (amount > 0) {
            this.clickTimeLeftArrow = 10;
            this.clickTimeRightArrow = -1;
        } else {
            this.clickTimeLeftArrow = -1;
            this.clickTimeRightArrow = 10;
        }

        this.updateShowArrows();
        return true;
    }

    private double getMaxLeftSliderPos() {
        return 0;
    }

    private double getMaxRightSliderPos() {
        return -(this.width -(Window.getWidth() /this.pixelSizeInScreen));
    }

    public void updateShowArrows() {
        this.isShowingLeftArrow = this.currentSliderPosX - this.getMaxLeftSliderPos() < -1;
        this.isShowingRightArrow = this.getMaxRightSliderPos() - this.currentSliderPosX < -1;
    }
}
