package ui.container;

import listener.MouseListener;
import org.lwjgl.glfw.GLFW;
import ui.widget.Tab;
import ui.widget.*;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.render.Shader;
import utils.render.Window;
import utils.render.texture.Textures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Representa el inventario que aparece en la parte inferior de la pantalla durante el juego..
 */
public class Inventory extends Container {
    /**
     * Píxeles in-game entre widgets.
     */
    public static final int WIDGET_SPACING = 4;


    /**
     * Altura del inventario, en unidades in-game, no píxeles de pantalla.
     */
    private float height;

    /**
     * Posición del inventario en el eje Y. El origen es la parte superior del inventario, por lo que de ahí para arriba
     * serían coordenadas negativas. El eje X se origina a la izquierda de la ventana.
     */
    private float posY;

    /**
     * Posición del scroll en el eje X.
     */
    private float currentScrollPosX = 0;

    /**
     * Indica si se están mostrando las felchas de scroll a la derecha e izquierda.
     */
    private boolean isShowingLeftArrow, isShowingRightArrow;

    /**
     * Indican el tiempo que están clicadas (solo graficamente, para facilitar que el usuario se de cuenta; si no, se
     * vería muy poco tiempo) las felcas de scroll cuando se hace clic que tienen las flechas de scroll, -1 si no están sindo clicadas.
     */
    private int clickTimeLeftArrow = -1, clickTimeRightArrow = -1;

    /**
     * Indican si el ratón está sobre las flechas de scroll.
     */
    private boolean isHoveredLeftArrow, isHoveredRightArrow;

    /**
     * Número de pestañas que hay actualmente en el inventario.
     */
    private int tabCount = 0;

    /**
     * Pestaña que está seleccionada actualmente.
     */
    private Tab currentTab = new Tab(null, null);

    /**
     * Mapa que almacena los widgets que tiene cada pestaña.
     */
    private final Map<Tab, List<Widget>> WIDGETS_PER_TAB = new HashMap<>();

    /**
     * Lista que contiene todas las pestañas del inventario.
     */
    private final List<Tab> TABS = new LinkedList<>();

    public Inventory() {
        this.updateShowArrows();
    }

    /**
     * Añade un Widget al inventario.
     * @param widget Widget que se quiere añadir.
     * @param tab Pestaña a la que se quiere añadir el widget.
     */
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
        if (widget instanceof OnResizeWindowEvent) {
            this.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
        }
    }

    /**
     * Añade un widget a todas las pestañas que hay en el inventario en ese momento.
     * @param widget Widget que se quiere añadir.
     */
    public void addWidgetToAllTabs(Widget widget) {
        this.TABS.forEach(tab -> this.addWidget(widget, tab));
    }

    /**
     * Añade una pestaña al inventario. También se puede añadir utilizando <code>Inventory#addWidget(tab, null)</code>.
     * @param tab Pestaña que se quiere añadir.
     * @see Inventory#addWidget(Widget, Tab)
     */
    public void addTab(Tab tab) {
        this.addWidget(tab, null);
    }

    /**
     * Elimina un widget de una pestaña específica.
     * @param widget Widget que se quiere eliminar.
     * @param tab Pestaña de la cuál se quiere eliminar el widget.
     */
    public void removeWidget(Widget widget, Tab tab) {
        List<Widget> widgetsInTab = this.WIDGETS_PER_TAB.getOrDefault(tab, new LinkedList<>());
        widgetsInTab.remove(widget);
        this.WIDGETS_PER_TAB.put(tab, widgetsInTab);
    }

    /**
     * Elimina un widget de todas las pestañas que haya en el inventario en ese momento.
     * @param widget Widget que se quiere eliminar.
     */
    public void removeWidget(Widget widget) {
        this.TABS.forEach(tab -> this.removeWidget(widget, tab));
    }

    @Override
    public void draw() {
        //Dibujamos el fondo del inventario.
        Textures.CONTAINER.draw(Shader.HUD,
                0f,
                this.posY,
                Window.getWidth(),
                this.height);

        //Dibujamos los widgets.
        super.widgets.forEach(widget -> {
            float widgetPosX, widgetPosY,
            widgetSizeX = Container.pixelSizeInScreen *widget.getBoundingBox().getWidth(),
            widgetSizeY = Container.pixelSizeInScreen *widget.getBoundingBox().getHeight();
            //Si tienen la anotacion IgnoreScrollMovement ignorarán el scroll.
            if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                widgetPosX = Container.pixelSizeInScreen *widget.getPosX();
                widgetPosY = Container.pixelSizeInScreen *widget.getPosY() +this.posY;
            } else {
                widgetPosX = Container.pixelSizeInScreen *(widget.getPosX() +this.currentScrollPosX);
                widgetPosY = Container.pixelSizeInScreen *widget.getPosY() +this.posY;
            }

            widget.getTexture().draw(Shader.HUD, widgetPosX, widgetPosY, widgetSizeX, widgetSizeY);

            if (widget instanceof CustomDrawWidget customDrawWidget) {
                if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                    customDrawWidget.draw(Container.pixelSizeInScreen, Container.pixelSizeInScreen * widget.getPosX(), Container.pixelSizeInScreen * widget.getPosY() + this.posY);
                } else {
                    customDrawWidget.draw(Container.pixelSizeInScreen, Container.pixelSizeInScreen * (widget.getPosX() + this.currentScrollPosX), Container.pixelSizeInScreen * widget.getPosY() + this.posY);
                }
            }
        });

        //Dibujamos las felchas de scroll.
        if (this.isShowingLeftArrow) {
            (this.clickTimeLeftArrow>-1? Textures.SELECTED_LEFT_ARROW: (this.isHoveredLeftArrow? Textures.HOVERED_LEFT_ARROW: Textures.UNSELECTED_LEFT_ARROW)).draw(Shader.HUD,
                    0, this.posY,
                    Container.pixelSizeInScreen * 7, this.height
                    );

            if (this.clickTimeLeftArrow > -1) {
                this.clickTimeLeftArrow--;
            }
        }

        if (this.isShowingRightArrow) {
            (this.clickTimeRightArrow>-1?Textures.SELECTED_RIGHT_ARROW:  (this.isHoveredRightArrow? Textures.HOVERED_RIGHT_ARROW: Textures.UNSELECTED_RIGHT_ARROW)).draw(Shader.HUD,
                    Window.getWidth() - Container.pixelSizeInScreen *7,
                    this.posY,
                    Container.pixelSizeInScreen * 7,
                    this.height
                    );

            if (this.clickTimeRightArrow > -1) {
                this.clickTimeRightArrow--;
            }
        }
    }

    @Override
    public boolean onMouseMoveEvent(float mouseX, float mouseY) {
        //Calculamos las coordenadas del ratón en coordendas in-game, con el origen en la parte superior izquierda del inventario.
        float interfaceX = (mouseX / Container.pixelSizeInScreen) - this.currentScrollPosX;
        float interfaceY = (mouseY - this.posY) / Container.pixelSizeInScreen;

        //Miramos si el ratón está sobre la flecha de scroll izquierda.
        if (this.isShowingLeftArrow) {
            if (interfaceX + this.currentScrollPosX <= 7 && interfaceY >= 0) {
                    this.isHoveredLeftArrow = true;
                    return true;
            }
        }

        //Miramos si el ratón está sobre la flecha de scroll derecha.
        if (this.isShowingRightArrow) {
            if (interfaceX +this.currentScrollPosX >= Window.getWidth() /Container.pixelSizeInScreen -7 && interfaceY >= 0) {
                this.isHoveredRightArrow = true;
                return true;
            }
        }

        //Miramos si el ratón está sobre algún widget.
        boolean isMouseOnAnyWidget = false;
        for (Widget widget: this.widgets) {
            boolean isMouseInWidget;
            if (widget.getClass().isAnnotationPresent(IgnoreScrollMovement.class)) {
                isMouseInWidget = widget.getBoundingBox().containsLocation((mouseX / Container.pixelSizeInScreen), interfaceY);
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

        //Si el ratón no está sobre ningún widget miramos si está dentro del inventario (dado que ocupa toda la pantalla en el eje X, sólo hay que comprobar el eje Y.
        return isMouseOnAnyWidget || interfaceY >= 0;
    }

    @Override
    public void onHoverEvent() {
        super.onHoverEvent();

        //Si se está clicando algna feecha de scroll, movemos la posición del scroll.
        if (MouseListener.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            if (this.isHoveredLeftArrow) {
                this.moveScroll(.7f);
            } else if (this.isHoveredRightArrow) {
                this.moveScroll(-.7f);
            }
        }
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        super.onResizeWindowEvent(newWidth, newHeight);

        //Recalculamos el tamaño de los pixeles in-game y recolocamos todas las pestañas para que sigan estando en el centro.
        this.height = 41 *Container.pixelSizeInScreen;
        this.posY = newHeight -(41 *Container.pixelSizeInScreen);
        this.updateShowArrows();

        float tabOrigin = ((newWidth /Container.pixelSizeInScreen) /(float) 2) - ((this.tabCount * Tab.TOTAL_TAB_SIZE) /(float) 2);

        Tab tab;
        for (int tabIndex = 0; tabIndex < this.TABS.size(); tabIndex++) {
            tab = this.TABS.get(tabIndex);

            tab.setPosY(Tab.WIDGET_OFFSET_Y);
            tab.setPosX(tabOrigin);
            tabOrigin += Tab.TOTAL_TAB_SIZE;
        }
    }

    /**
     * Mueve el scroll siempre que se pueda (no puedes mover el scroll si no hay componentes que mostrar).
     * @param amount Cantiddad de píxeles in-game que se quiere mover el scroll, positivo para mover a la izquierda,
     * negativo para la derecha, y 0 si no se quiere mover.
     * @return Si ha sido posible mover el scroll o no.
     */
    public boolean moveScroll(float amount) {
        float originalSliderPos = this.currentScrollPosX;

        this.currentScrollPosX += amount;
        if (this.currentScrollPosX > this.getMaxLeftScrollPos() || this.currentScrollPosX <= this.getMaxRightScrollPos()) {
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

    /**
     * @return Valor mínimo que puede tener el scroll, siempre es 0.
     */
    private double getMaxLeftScrollPos() {
        return 0;
    }

    /**
     * @return Valor máximo que puede tener el scroll.
     */
    private double getMaxRightScrollPos() {
        return -(this.currentTab.getWidth() -(Window.getWidth() /Container.pixelSizeInScreen));
    }

    /**
     * Recalcula si se van a mostrar o no las flechas de scroll. Las felchas se muestran cuando el scroll se puede mover en
     * su dirección.
     */
    public void updateShowArrows() {
        this.isShowingLeftArrow = this.currentScrollPosX - this.getMaxLeftScrollPos() < -1;
        this.isShowingRightArrow = this.getMaxRightScrollPos() - this.currentScrollPosX < -1;
    }

    /**
     * Deselecciona todas las pestañas. Actualiza solo la parte gráfica.
     */
    public void unselectAllTabs() {
        this.TABS.forEach(tab -> tab.setSelected(false));
    }

    /**
     * Establece la pestaña seleccionada. Deselecciona todas las pestañas, selecciona la que se le pasa como parámetro y
     * actualiza los widgets que hay que mostrar.
     * @param currentTab
     */
    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
        this.currentTab.setSelected(true);
        
        super.widgets = this.WIDGETS_PER_TAB.getOrDefault(this.currentTab, new LinkedList<>());
        super.widgets.addAll(this.TABS);

        this.currentScrollPosX = 0;
        this.updateShowArrows();
    }

    /**
     * Abre la pestaña correspondiente al índice.
     * @param index Índice de la pestaña que se quiere abrir, empieza en 1, no en 0.
     */
    public void goToTab(int index) {
        if (index >= 1 && index <= this.TABS.size()) {
            this.unselectAllTabs();
            this.setCurrentTab(this.TABS.get(index -1));
        }
    }
}
