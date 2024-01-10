package ui.widget;

import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Slot que contiene un icono. Cuando se hace clic se selecciona, y cuando se vuelve a clicar se desselecciona
 */
public class SlotWidget extends Widget implements CustomDrawWidget {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    public final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(16, 16);

    /**
     * Textura del icono del slot.
     */
    private final Texture CONTENT;

    /**
     * Indica si el slot está seleccionado o no.
     */
    private boolean isSelected = false;

    /**
     * @param posX Posición en el eje X, en coordenadas in-game.
     * @param posY Posición en el eje Y, en coordenadas in-game.
     * @param content Textura del icono del slot.
     */
    public SlotWidget(float posX, float posY, Texture content) {
        super(posX, posY, SlotWidget.BASE_BOUNDING_BOX);
        this.CONTENT = content;
    }

    @Override
    public Texture getTexture() {
        //Si está seleccionado utilizamos una textura, y si no, utilizamos otra.
        return this.isSelected? Textures.SELECTED_SLOT_WIDGET :(this.isHovered()? Textures.HOVERED_SLOT_WIDGET :Textures.UNSELECTED_SLOT_WIDGET);
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el icono.
        this.CONTENT.draw(Shader.HUD,
                posX + 2.5f * pixelSizeInScreen,
                posY + 2.5f * pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getWidth() -5) * pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getHeight() -5) * pixelSizeInScreen
                );
    }

    @Override
    public void onClickEvent() {
        this.setSelected(!this.isSelected());
        super.onClickEvent();
    }

    /**
     * Cambia si el slot está seleccionado o no.
     * @param isSelected Si el slot está seleccionado o no.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * @return Si el slot está seleccionado o no.
     */
    public boolean isSelected() {
        return this.isSelected;
    }
}
