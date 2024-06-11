package ui.widget;

import main.Main;
import ui.container.Inventory;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Widget que representa una pestaña del inventario. Cuando clicas en una pestaña se cambia el contenido del inventario.
 * @see IgnoreScrollMovement
 * @see Inventory
 * @see Widget
 */
@IgnoreScrollMovement
public class Tab extends Widget implements CustomDrawWidget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(26, 12);

    /**
     * Separación que tienen las tas pestañas entre ellas.
     */
    public static final int TAB_SPACING = 1;

    /**
     * Desplazamiento en el eje Y de las pestañas.
     */
    public static final float WIDGET_OFFSET_Y = -9.9f;

    /**
     * Distancia entre el origen de una pestaña y el origen de la siguiente.
     */
    public static final float TOTAL_TAB_SIZE = Tab.TAB_SPACING + Tab.BASE_BOUNDING_BOX.getWidth();
    private float width = 0;
    private boolean isSelected = false;
    private final Texture SELECTED_CONTENT, UNSELECTED_CONTENT;

    /**
     * Constructor de la clase Tab que inicializa una pestaña con contenido seleccionado y no seleccionado.
     *
     * @param selectedContent   Textura del contenido cuando la pestaña está seleccionada.
     * @param unselectedContent Textura del contenido cuando la pestaña no está seleccionada.
     */
    public Tab(Texture selectedContent, Texture unselectedContent) {
        super(0, 0, Tab.BASE_BOUNDING_BOX);
        this.SELECTED_CONTENT = selectedContent;
        this.UNSELECTED_CONTENT = unselectedContent;
    }

    /**
     * Actualiza el ancho de la pestaña. Se ajusta el ancho de la pestaña al mayor entre el ancho actual y el nuevo ancho máximo (sumando una separación al final).
     *
     * @param maxWidth El nuevo ancho máximo que se desea establecer para la pestaña.
     */
    public void updateWidth(float maxWidth) {
        this.width = Math.max(this.width, maxWidth + Inventory.WIDGET_SPACING);
    }

    /**
     * Obtiene el ancho actual de la pestaña.
     *
     * @return El ancho actual de la pestaña.
     */
    public float getWidth() {
        return this.width;
    }

    /**
     * Establece si la pestaña está seleccionada o no.
     *
     * @param isSelected true si la pestaña está seleccionada, false de lo contrario.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public Texture getTexture() {
        return this.isSelected? Textures.SELECTED_TAB: Textures.UNSELECTED_TAB;
    }

    @Override
    public void onMouseClickEvent() {
        //Si clicas una pestaña se desseleccionan el resto y se establece la pestaña que acaba de ser clicada como la actual.
        if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
            inventory.unselectAllTabs();
            this.isSelected = true;
            inventory.setCurrentTab(this);
        }
        super.onMouseClickEvent();
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibuja el icono de la pestaña. Dependiendo de si está clicada o no se utiliza una textura u otra.
        ((this.isHovered() || this.isSelected)? this.SELECTED_CONTENT: this.UNSELECTED_CONTENT).draw(Shader.HUD,
                posX +2f *pixelSizeInScreen,
                posY +1f *pixelSizeInScreen,
                (Tab.BASE_BOUNDING_BOX.getWidth() -4) *pixelSizeInScreen,
                (Tab.BASE_BOUNDING_BOX.getHeight() -3) *pixelSizeInScreen
                );
    }
}
