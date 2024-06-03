package ui.tooltip;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ui.container.Container;
import utils.render.Shader;
import utils.render.Window;
import utils.render.mesh.TooltipMesh;
import utils.render.texture.Texture;
import world.location.Location;

import java.util.LinkedHashMap;

public class Tooltip {
    private Vector2f size = new Vector2f(0, 0);

    private LinkedHashMap<Texture, Vector2f> textures = new LinkedHashMap<>();

    public void show(Location tooltipLocation) {
        float tooltipPixelSize = Container.pixelSizeInScreen;
        tooltipLocation.add(8*tooltipPixelSize, 0);

        if (tooltipLocation.getY() +this.size.y() > Window.getHeight()) {
            tooltipLocation.add(0, -(tooltipLocation.getY() +this.size.y() -Window.getHeight()));
        }

        TooltipMesh mesh;

        Shader.SHAPE.use();
        Shader.SHAPE.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.SHAPE.uploadMatrix4f("uView", new Matrix4f().identity());
        Shader.SHAPE.upload2f("uPosition", tooltipLocation.getX(), tooltipLocation.getY());
        Shader.SHAPE.uploadInt("uUseCameraParameters", 0);

        Shader.SHAPE.upload4f("uColor", 0.46f, 0.48f, 0.43f, 0.7f);

        mesh = new TooltipMesh();
        mesh.addVertex(-3, -2, (size.x() /tooltipPixelSize) +6, (size.y() /tooltipPixelSize) +4, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        mesh = new TooltipMesh();
        mesh.addVertex(-2, -3, (size.x() /tooltipPixelSize) +4, (size.y() /tooltipPixelSize) +6, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        Shader.SHAPE.upload4f("uColor", 0.17f, 0.17f, 0.16f, 0.5f);

        mesh = new TooltipMesh();
        mesh.addVertex(-2, -2, (size.x() /tooltipPixelSize) +4, (size.y() /tooltipPixelSize) +4, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        textures.forEach((texture, position) -> {
            texture.draw(
                    Shader.HUD,
                    tooltipLocation.getX() +position.x(),
                    tooltipLocation.getY() +position.y(),
                    texture.getSize().x(),
                    texture.getSize().y()
            );
        });
    }

    public void show() {
        float tooltipPixelSize = Container.pixelSizeInScreen;
        Location tooltipLocation = MouseListener.getInScreenLocation().add(8*tooltipPixelSize, 0);

        if (tooltipLocation.getY() +this.size.y() > Window.getHeight()) {
            tooltipLocation.add(0, -(tooltipLocation.getY() +this.size.y() -Window.getHeight()));
        }

        TooltipMesh mesh;

        Shader.SHAPE.use();
        Shader.SHAPE.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.SHAPE.uploadMatrix4f("uView", new Matrix4f().identity());
        Shader.SHAPE.upload2f("uPosition", tooltipLocation.getX(), tooltipLocation.getY());
        Shader.SHAPE.uploadInt("uUseCameraParameters", 0);

        Shader.SHAPE.upload4f("uColor", 0.46f, 0.48f, 0.43f, 0.7f);

        mesh = new TooltipMesh();
        mesh.addVertex(-3, -2, (size.x() /tooltipPixelSize) +6, (size.y() /tooltipPixelSize) +4, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        mesh = new TooltipMesh();
        mesh.addVertex(-2, -3, (size.x() /tooltipPixelSize) +4, (size.y() /tooltipPixelSize) +6, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        Shader.SHAPE.upload4f("uColor", 0.17f, 0.17f, 0.16f, 0.5f);

        mesh = new TooltipMesh();
        mesh.addVertex(-2, -2, (size.x() /tooltipPixelSize) +4, (size.y() /tooltipPixelSize) +4, tooltipPixelSize);
        mesh.load();
        mesh.draw();

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        textures.forEach((texture, position) -> {
            texture.draw(
                    Shader.HUD,
                    tooltipLocation.getX() +position.x(),
                    tooltipLocation.getY() +position.y(),
                    texture.getSize().x(),
                    texture.getSize().y()
            );
        });
    }

    public void addTexture(Vector2f position, Texture texture) {
        this.textures.put(texture, position);

        size.set(
                Math.max(size.x(), position.x() + texture.getSize().x()),
                Math.max(size.y(), position.y() + texture.getSize().y())
        );


    }
}
