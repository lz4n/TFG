package ui;

import listener.MouseListener;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ui.widget.*;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import ui.widget.widgetUtils.RelocatableWhenResizedScreen;
import utils.render.Shader;
import utils.render.Window;
import utils.render.mesh.Mesh;
import utils.render.texture.Textures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Representa un inventario y se encarga de registrar los clicks y dibujar el inventario en pantalla.
 */
public class Inventory {
    public static final int WIDGET_SPACING = 4;


    /**
     * Altura del inventario, en unidades in-game, no píxeles de pantalla.
     */
    private float height;

    /**
     * Posición del inventario en el eje Y. Se cuenta de arriba a abajo.
     */
    private float posY;

    private float currentScrollPosX = 0;

    /**
     * Píxeles de patalla que ocupa un pixel de la interfaz in-game.
     */
    public float pixelSizeInScreen;

    private boolean isShowingLeftArrow, isShowingRightArrow;
    private int clickTimeLeftArrow = -1, clickTimeRightArrow = -1;
    private boolean isHoveredLeftArrow, isHoveredRightArrow;
    private int tabCount = 0;
    private Tab currentTab = new Tab(null, null);

    private final Map<Tab, List<Widget>> WIDGETS_PER_TAB = new HashMap<>();

    private final List<Tab> TABS = new LinkedList<>();
    private List<Widget> currentWidgets = new LinkedList<>();

    public Inventory() {
        this.updateShowArrows();
    }


    public void addWidget(Widget widget, Tab tab) {
        if (!widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
            tab.updateWidth(widget.getBoundingBox().getComponents().z());
        }

        if (widget instanceof Tab newTab) {
            this.tabCount++;
            this.TABS.add(newTab);
        }
        if (tab != null) {
            List<Widget> widgetsInTab = this.WIDGETS_PER_TAB.getOrDefault(tab, new LinkedList<>());
            widgetsInTab.add(widget);
            this.WIDGETS_PER_TAB.put(tab, widgetsInTab);
        }
        if (widget instanceof RelocatableWhenResizedScreen) {
            this.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
        }
    }

    public void addWidgetToAllTabs(Widget widget) {
        this.TABS.forEach(tab -> this.addWidget(widget, tab));
    }

    public void addTab(Tab tab) {
        this.addWidget(tab, null);
    }

    public void removeWidget(Widget widget, Tab tab) {
        List<Widget> widgetsInTab = this.WIDGETS_PER_TAB.getOrDefault(tab, new LinkedList<>());
        widgetsInTab.remove(widget);
        this.WIDGETS_PER_TAB.put(tab, widgetsInTab);
    }

    public void removeWidget(Widget widget) {
        this.TABS.forEach(tab -> this.removeWidget(widget, tab));
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


        this.currentWidgets.forEach(widget -> {
            if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                Shader.HUD.upload2f("uHudPosition", this.pixelSizeInScreen * widget.getPosX(), this.pixelSizeInScreen * widget.getPosY() + this.posY);
                Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());
            } else {
                Shader.HUD.upload2f("uHudPosition", this.pixelSizeInScreen * (widget.getPosX() + this.currentScrollPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY);
                Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());
            }

            widget.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(mesh.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, mesh.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                    customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * widget.getPosX(), this.pixelSizeInScreen * widget.getPosY() + this.posY, this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());
                } else {
                    customDrawWidget.draw(mesh, this.pixelSizeInScreen, this.pixelSizeInScreen * (widget.getPosX() + this.currentScrollPosX), this.pixelSizeInScreen * widget.getPosY() + this.posY, this.pixelSizeInScreen * widget.getBoundingBox().getWidth(), this.pixelSizeInScreen * widget.getBoundingBox().getHeight());
                }
            }
        });

        if (this.isShowingLeftArrow) {
            Shader.HUD.upload2f("uHudPosition", 0, this.posY);
            Shader.HUD.upload2f("uHudSize", this.pixelSizeInScreen * 7, this.height);

            (this.clickTimeLeftArrow>-1? Textures.SELECTED_LEFT_ARROW: (this.isHoveredLeftArrow? Textures.HOVERED_LEFT_ARROW: Textures.UNSELECTED_LEFT_ARROW)).bind();
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

            (this.clickTimeRightArrow>-1?Textures.SELECTED_RIGHT_ARROW:  (this.isHoveredRightArrow? Textures.HOVERED_RIGHT_ARROW: Textures.UNSELECTED_RIGHT_ARROW)).bind();
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

    public boolean onMouseMoveEvent(float mouseX, float mouseY) {
        float interfaceX = (mouseX / this.pixelSizeInScreen) - this.currentScrollPosX;
        float interfaceY = (mouseY - this.posY) / this.pixelSizeInScreen;

        if (this.isShowingLeftArrow) {
            if (interfaceX + this.currentScrollPosX <= 7 && interfaceY >= 0) {
                    this.isHoveredLeftArrow = true;
                    return true;
            }
        }

        if (this.isShowingRightArrow) {
            if (interfaceX +this.currentScrollPosX >= Window.getWidth() /this.pixelSizeInScreen -7 && interfaceY >= 0) {
                this.isHoveredRightArrow = true;
                return true;
            }
        }

        boolean isMouseOnAnyWidget = false;
        for (Widget widget: this.currentWidgets) {
            boolean isMouseInWidget;
            if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                isMouseInWidget = widget.getBoundingBox().containsLocation((mouseX / this.pixelSizeInScreen), interfaceY);
            } else {
                isMouseInWidget = widget.getBoundingBox().containsLocation(interfaceX, interfaceY);
            }

            if (isMouseInWidget) {
                widget.setHovered(true);
                isMouseOnAnyWidget = true;
            } else {
                widget.setHovered(false);
            }
        }

        return isMouseOnAnyWidget || interfaceY >= 0;
    }

    public void onHoverEvent() {
        if (MouseListener.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            if (this.isHoveredLeftArrow) {
                this.moveSlider(.7f);
            } else if (this.isHoveredRightArrow) {
                this.moveSlider(-.7f);
            }
        }


        this.currentWidgets.stream()
                .filter(Widget::isHovered)
                .forEach(Widget::onHoverEvent);
    }

    /**
     * Evento que se llama cuando se hace clic en la interfaz y se lo pasa el Widget que se haya pulsado, siempre que
     * implemente la interfaz <code>ClickableWidget</code>
     * @param mouseX Posición en el eje X del ratón, en píxeles de pantalla.
     * @param mouseY Posición en el eje Y del ratón, en píxeles de pantalla.
     */
    public void onClickEvent(float mouseX, float mouseY) {
        new LinkedList<>(this.currentWidgets).stream()
                .filter(Widget::isHovered)
                .forEach(Widget::onClickEvent);
    }

    public void onResizeWindowEvent(float newWidth, float newHeight) {
        this.pixelSizeInScreen = Window.getHeight() / 250f;
        this.height = 41 *this.pixelSizeInScreen;
        this.posY = Window.getHeight() -(41 *this.pixelSizeInScreen);
        this.updateShowArrows();

        float tabOrigin = ((newWidth /this.pixelSizeInScreen) /(float) 2) - ((this.tabCount * Tab.TOTAL_TAB_SIZE) /(float) 2);

        Tab tab;
        for (int tabIndex = 0; tabIndex < this.TABS.size(); tabIndex++) {
            tab = this.TABS.get(tabIndex);

            tab.setPosY(Tab.WIDGET_OFFSET_Y);
            tab.setPosX(tabOrigin);
            tabOrigin += Tab.TOTAL_TAB_SIZE;
        }

        this.currentWidgets.stream()
                .filter(widget -> widget instanceof RelocatableWhenResizedScreen)
                .forEach(widget -> ((RelocatableWhenResizedScreen) widget).onResizeWindowEvent(newWidth /this.pixelSizeInScreen, newHeight /this.pixelSizeInScreen));
    }

    public boolean moveSlider(float amount) {
        float originalSliderPos = this.currentScrollPosX;

        this.currentScrollPosX += amount;
        if (this.currentScrollPosX > this.getMaxLeftSliderPos() || this.currentScrollPosX <= this.getMaxRightSliderPos()) {
            this.currentScrollPosX = originalSliderPos;
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
        return -(this.currentTab.getWidth() -(Window.getWidth() /this.pixelSizeInScreen));
    }

    public void updateShowArrows() {
        this.isShowingLeftArrow = this.currentScrollPosX - this.getMaxLeftSliderPos() < -1;
        this.isShowingRightArrow = this.getMaxRightSliderPos() - this.currentScrollPosX < -1;
    }

    public void unselectAllTabs() {
        this.TABS.forEach(tab -> tab.setSelected(false));
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
        this.currentTab.setSelected(true);
        
        this.currentWidgets = this.WIDGETS_PER_TAB.getOrDefault(this.currentTab, new LinkedList<>());
        this.currentWidgets.addAll(this.TABS);

        this.currentScrollPosX = 0;
        this.updateShowArrows();
    }
}
