package ui.widget;

import main.Main;
import ui.container.Inventory;
import ui.widget.widgetUtils.CustomDrawWidget;
import ui.widget.widgetUtils.IgnoreScrollMovement;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Indicador que se muestra en pantalla con una cruz y un icono. Cuando se hace clic sobre el se cierra.
 */
@IgnoreScrollMovement
public class ScreenIndicatorWidget extends Widget implements OnResizeWindowEvent, CustomDrawWidget {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(16, 29);

    /**
     * Posición en los ejes X e Y. La posición se calcula respecto a la esquina superior izquierda de la ventana, no del inventario.
     */
    private final float RELATIVE_X, RELATIVE_Y;

    /**
     * Icono que se muestra en el indicador.
     */
    private final Texture CONTENT;

    /**
     * @param relativeX Posición en el eje X. La posición se calcula respecto a la esquina superior izquierda de la ventana, no del inventario.
     * @param relativeY Posición en el eje Y. La posición se calcula respecto a la esquina superior izquierda de la ventana, no del inventario.
     * @param content Textura del incono del indicador.
     */
    public ScreenIndicatorWidget(float relativeX, float relativeY, Texture content) {
        super(0, 0, ScreenIndicatorWidget.BASE_BOUNDING_BOX);
        this.RELATIVE_X = relativeX;
        this.RELATIVE_Y = relativeY;
        this.CONTENT = content;
    }

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el icono.
        this.CONTENT.draw(Shader.HUD,
                posX + 2.5f * pixelSizeInScreen,
                posY + 15.5f *pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getWidth() -5) *pixelSizeInScreen,
                (SlotWidget.BASE_BOUNDING_BOX.getHeight() -5) *pixelSizeInScreen
                );
    }

    @Override
    public Texture getTexture() {
        return this.isHovered()? Textures.HOVERED_SCREEN_INDICATOR: Textures.SCREEN_INDICATOR;
    }

    @Override
    public void onClickEvent() {
        //Al clicar se cierra el indicador.
        if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
            inventory.removeWidget(this);
        }
        super.onClickEvent();
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        //Cómo las coordenadas son respecto a la ventana y no el inventario,
        this.setPosX(newWidth -this.RELATIVE_X);
        this.setPosY(newHeight -this.RELATIVE_Y);
    }
}
