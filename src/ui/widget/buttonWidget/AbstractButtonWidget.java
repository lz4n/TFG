package ui.widget.buttonWidget;

import org.joml.Vector2f;
import ui.widget.Widget;
import ui.widget.widgetUtils.CustomDrawWidget;
import utils.BoundingBox;
import utils.render.Shader;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;

/**
 * Representa un botón abstracto, con contenido y que se puede clicar.
 */
public abstract class AbstractButtonWidget extends Widget implements CustomDrawWidget {
    /**
     * Tiempo que se mantiene clicado el botón, para facilitar que el usuario se de cuenta de que se ha clicado el botón.
     */
    private static final int CLICK_TIME = 15;

    /**
     * Textura del botón cuando está clicado y cuando no.
     */
    private final Texture TEXTURE, CLICKED_TEXTURE;

    /**
     * Desplazamiento que tiene el contenido del botón cuando se hace hover.
     */
    private final Vector2f HOVER_OFFSET;

    /**
     * Tiempo que va a aparecer el botón como clicado, si es negativo se entiende que el botón no está clicado.
     * @see AbstractButtonWidget#CLICK_TIME
     */
    protected byte clickTime = -1;

    /**
     * @param posX Posición en el eje X del botón.
     * @param posY Posición en el eje Y del botón.
     * @param texture Textura que tiene el botón cuando <strong>no está clicado</strong>.
     * @param clickedTexture Textura que tiene el botón cuando <strong>si está clicado</strong>.
     * @param clickOffset Desplazamiento que tiene el contenido del botón cuando se mantiene el ratón sobre el botón.
     * @param baseBoundingBox BoundingBox base del botón.
     */
    public AbstractButtonWidget(float posX, float posY, Texture texture, Texture clickedTexture, Vector2f clickOffset, BoundingBox baseBoundingBox) {
        super(posX, posY, baseBoundingBox);
        this.TEXTURE = texture;
        this.CLICKED_TEXTURE = clickedTexture;
        this.HOVER_OFFSET = clickOffset;
    }

    @Override
    public Texture getTexture() {
        //Si está clicado muestra CLICKED_TEXTURE, y si no TEXTURE.
        return this.clickTime > -1 ? this.CLICKED_TEXTURE : this.TEXTURE;
    }

    /**
     * @return Devuelve el contenido del botón.
     */
    public abstract Texture getIcon();

    @Override
    public void draw(float pixelSizeInScreen, float posX, float posY) {
        //Dibujamos el icono.
        if (this.getIcon() != null) {
            if (this.getIcon() instanceof Graphics2dTexture) {
                this.getIcon().draw(Shader.HUD,
                        posX +((this.getBoundingBox().getWidth() -this.getIcon().getSize().x() /pixelSizeInScreen) /2f +(this.isHovered()?this.HOVER_OFFSET.x():0)) *pixelSizeInScreen,
                        posY +((this.getBoundingBox().getHeight() -this.getIcon().getSize().y() /pixelSizeInScreen) /2f +(this.isHovered()?this.HOVER_OFFSET.y():0)) *pixelSizeInScreen,
                        this.getIcon().getSize().x(),
                        this.getIcon().getSize().y()
                );
            } else {
                this.getIcon().draw(Shader.HUD,
                        posX +((this.getBoundingBox().getWidth() -this.getIcon().getSize().x()) /2f +(this.isHovered()?this.HOVER_OFFSET.x():0)) *pixelSizeInScreen,
                        posY +((this.getBoundingBox().getHeight() -this.getIcon().getSize().y()) /2f +(this.isHovered()?this.HOVER_OFFSET.y():0)) *pixelSizeInScreen,
                        this.getIcon().getSize().x() *pixelSizeInScreen,
                        this.getIcon().getSize().y() *pixelSizeInScreen
                );
            }
        }

        //Vamos restando 1 a clickTime, hasta llegar a -1
        if (this.clickTime > -1) {
            this.clickTime--;
        }
    }

    @Override
    public void onClickEvent() {
        super.onClickEvent();
        this.clickTime = AbstractButtonWidget.CLICK_TIME;
    }
}
