package game.utils.player.ui.tooltip;

import game.listener.MouseListener;
import game.utils.player.ui.container.Container;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import game.utils.render.Shader;
import game.utils.render.Window;
import game.utils.render.mesh.ShapeMesh;
import game.utils.render.texture.Graphics2dTexture;
import game.utils.render.texture.Texture;
import game.world.location.Location;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase base para la creación y visualización de tooltips en una interfaz de usuario.
 * Los tooltips pueden mostrar texturas y se ajustan automáticamente para evitar salir de los límites de la ventana.
 */
public class Tooltip {
    private Vector2f size = new Vector2f(0, 0);

    private LinkedHashMap<Texture, Vector2f> textures = new LinkedHashMap<>();

    private void show(Location tooltipLocation) {

        if (tooltipLocation.getX() < 0) {
            tooltipLocation.setX(0);
        }

        if (tooltipLocation.getY() < 0) {
            tooltipLocation.setY(0);
        }

        if (tooltipLocation.getX() +this.size.x() > Window.getWidth()) {
            tooltipLocation.add(-(tooltipLocation.getX() +this.size.x() -Window.getWidth()), 0);
        }

        if (tooltipLocation.getY() +this.size.y() > Window.getHeight()) {
            tooltipLocation.add(0, -(tooltipLocation.getY() +this.size.y() -Window.getHeight()));
        }

        ShapeMesh mesh;

        Shader.SHAPE.use();
        Shader.SHAPE.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.SHAPE.uploadMatrix4f("uView", new Matrix4f().identity());
        Shader.SHAPE.upload2f("uPosition", tooltipLocation.getX(), tooltipLocation.getY());

        Shader.SHAPE.upload4f("uColor", 0.46f, 0.48f, 0.43f, 0.7f);

        mesh = new ShapeMesh();
        mesh.addVertex(-3, -2, (size.x() / Container.pixelSizeInScreen) +6, (size.y() /Container.pixelSizeInScreen) +4, Container.pixelSizeInScreen);
        mesh.load();
        mesh.draw();

        mesh = new ShapeMesh();
        mesh.addVertex(-2, -3, (size.x() /Container.pixelSizeInScreen) +4, (size.y() /Container.pixelSizeInScreen) +6, Container.pixelSizeInScreen);
        mesh.load();
        mesh.draw();

        Shader.SHAPE.upload4f("uColor", 0.17f, 0.17f, 0.16f, 0.5f);

        mesh = new ShapeMesh();
        mesh.addVertex(-2, -2, (size.x() /Container.pixelSizeInScreen) +4, (size.y() /Container.pixelSizeInScreen) +4, Container.pixelSizeInScreen);
        mesh.load();
        mesh.draw();

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        for (Map.Entry<Texture, Vector2f> entry : textures.entrySet()) {
            Texture texture = entry.getKey();
            Vector2f position = entry.getValue();
            texture.draw(
                    Shader.HUD,
                    tooltipLocation.getX() + position.x(),
                    tooltipLocation.getY() + position.y(),
                    texture.getSize().x(),
                    texture.getSize().y()
            );
        }
    }

    /**
     * Muestra el tooltip en la ubicación de pantalla correspondiente a la ubicación en el mundo especificada.
     *
     * @param location Ubicación en el mundo donde se mostrará el tooltip.
     */
    public void showAtWorld(Location location) {
        this.show(location.getInScreenCoordsRelative());
    }

    /**
     * Muestra el tooltip en la ubicación actual del mouse más un desplazamiento.
     * Útil para tooltips que siguen al mouse con un pequeño desfase.
     */
    public void showAtMouse() {
        this.show(MouseListener.getInScreenLocation().add(8*Container.pixelSizeInScreen, 0));
    }

    /**
     * Agrega una textura al tooltip en la posición especificada.
     *
     * @param position Posición relativa donde se colocará la textura dentro del tooltip.
     * @param texture  Textura a agregar.
     */
    public void addTexture(Vector2f position, Texture texture) {
        this.textures.put(texture, position);

        size.set(
                Math.max(size.x(), position.x() + texture.getSize().x()),
                Math.max(size.y(), position.y() + texture.getSize().y())
        );
    }

    /**
     * Remueve todas las texturas del tooltip y libera los recursos gráficos asociados.
     */
    public void remove() {
        for (Texture textre : this.textures.keySet()) {
            if (textre instanceof Graphics2dTexture graphics2dTexture) {
                graphics2dTexture.remove();
            }
        }

        this.textures.clear();
    }
}
