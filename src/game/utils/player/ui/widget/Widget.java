package game.utils.player.ui.widget;

import game.utils.player.ui.container.Container;
import game.utils.player.ui.container.Frame;
import game.utils.player.ui.container.Inventory;
import game.utils.player.ui.tooltip.Tooltip;
import game.utils.BoundingBox;
import game.utils.render.texture.Texture;
import game.utils.player.ui.widget.widgetUtils.WidgetEvent;

/**
 * Representación abstracta de un widget. Un widget es un elemento que se coloca en un contenedor (frame o inventario),
 * que permite interactural con él, ya sea mediante clic o pasando el ratón por encima (hover).
 * @see Container
 * @see Inventory
 * @see Frame
 */
public abstract class Widget {
    private float posX, posY;

    /**
     * Indica si el ratón está sobre el widget.
     */
    protected boolean isHovered = false;
    private BoundingBox boundingBox;
    private WidgetEvent onClickEvent = () -> {}, onHoverEvent = () -> {};
    private Tooltip tooltip = null;

    /**
     * @param posX Posición en el eje X, en coordendas in-game, con el origen de coordenadas que tenga el contenedor.
     * @param posY Posición en el eje X, en coordendas in-game, con el origen de coordenadas que tenga el contenedor.
     * @param baseBoundingBox BoundingBox base del widget, esta BoundingBox no se modifica, se realiza una clonación y
     * se modifica el nuevo objeto.
     */
    public Widget(float posX, float posY, BoundingBox baseBoundingBox) {
        this.posX = posX;
        this.posY = posY;
        this.boundingBox = baseBoundingBox.clone().move(posX, posY);
    }

    /**
     * @return Posición en el eje X.
     * @see Widget#posX
     */
    public float getPosX() {
        return this.posX;
    }

    /**
     * Cambia la posición en el eje X y actualiza la BoundingBox.
     * @param posX Nueva posición en el eje X.
     * @see Widget#posX
     */
    public void setPosX(float posX) {
        this.boundingBox.move(-this.posX, 0);
        this.boundingBox.move(posX, 0);
        this.posX = posX;
    }

    /**
     * @return Posición en el eje Y.
     * @see Widget#posY
     */
    public float getPosY() {
        return this.posY;
    }

    /**
     * Cambia la posición en el eje Y y actualiza la BoundingBox.
     * @param posY Nueva posición en el eje Y.
     * @see Widget#posY
     */
    public void setPosY(float posY) {
        this.boundingBox.move(0, -this.posY).move(0, posY);
        this.posY = posY;
    }

    /**
     * @return Una copia del BoundingBox del widget.
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox.clone();
    }

    /**
     * @return Textura que está utilizando el widget en el momento de la llamada.
     */
    public abstract Texture getTexture();

    /**
     * Establece si el ratón está sobre el widget.
     * @param isHovered Si el ratón está sobre el widget o no.
     */
    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
    }

    /**
     * @return Si el ratón está sobre el widget o no.
     */
    public boolean isHovered() {
        return this.isHovered;
    }

    /**
     * Establece el evento de clic.
     * @param onClickEvent Evento que se llama cada vez que se haga clic en el widget.
     */
    public void setOnClickEvent(WidgetEvent onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    /**
     * Establece el evento de hover.
     * @param onHoverEvent Evento que se llama cada vez que se hace hover.
     */
    public void setOnHoverEvent(WidgetEvent onHoverEvent) {
        this.onHoverEvent = onHoverEvent;
    }

    /**
     * Llama al evento de hover.
     */
    public void onHoverEvent() {
        this.onHoverEvent.widgetEvent();
    }

    /**
     * Llama al evento de clic.
     */
    public void onMouseClickEvent() {
        this.onClickEvent.widgetEvent();
    }

    /**
     * Obtiene el tooltip asociado a este componente.
     *
     * @return El objeto Tooltip asociado a este componente.
     */
    public Tooltip getTooltip() {
        return tooltip;
    }

    /**
     * Establece el tooltip para este componente.
     *
     * @param tooltip El tooltip que se desea establecer.
     */
    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }
}
