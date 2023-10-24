package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import ui.Inventory;
import ui.widget.SeparatorWidget;
import ui.widget.SlotWidget;
import ui.widget.TextWidget;
import utils.Time;
import utils.render.Window;
import utils.render.mesh.*;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.WorldGenerator;
import world.entity.Entity;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * Escena encargada de renderizar el mundo.
 *
 * @author Izan
 */
public class WorldScene extends Scene {
    /**
     * Tamaño base (sin contar el zoom) de los sprites.
     */
    public static final int SPRITE_SIZE = 20;

    /**
     * Camara de la escena.
     */
    public static Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    /**
     * Textura del selector del ratón
     */
    private static final Texture MOUSE_TEXTURE = new StaticTexture("assets/textures/ui/selector.png");

    /**
     * <code>Mesh</code> anónimo utilizado para el selector del ratón.
     * @see Mesh
     */
    private final MouseSelectionMesh MOUSE_SELECTION_MESH = new MouseSelectionMesh(3, 2);

    /**
     * Mesh utilizado para dibujar el HUD.
     */
    private final HUDMesh HUD_MESH = new HUDMesh();

    /**
     * Inventario del jugador en la escena.
     */
    private final Inventory INVENTORY = new Inventory();

    @Override
    public void init() {
        new WorldGenerator(Main.WORLD, this).run();

        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().adjust();
        }

        drawTerrain();

        HUD_MESH.load();

        WorldScene.CAMERA.moveCamera(new Vector2f((float) Main.WORLD.getSize() / 2));

        //Generamos la estructura de widgets del inventario.
        this.INVENTORY.addWidget(new SeparatorWidget(20, 0));
        this.INVENTORY.addWidget(new SlotWidget(40, 4, Feature.FeatureType.TREE.getTexture()));
        this.INVENTORY.addWidget(new TextWidget(40, 23, "kkkkkkkkk"));
    }

    /**
     * Actualiza y sube a la <code>GPU</code> el <code>mesh</code> del selector.
     * @param x Posición en el eje X in-game del selector.
     * @param y Posición en el eje Y in-game del selector.
     */
    public void updateSelection(int x, int y) {
        int sizeX = 1, sizeY = 1;
        Feature selectedFeature = new Location(x, y).getFeature();
        if (selectedFeature != null) {
            x = (int) selectedFeature.getLocation().getX();
            y = (int) selectedFeature.getLocation().getY();
            sizeX = selectedFeature.getSize().x();
            sizeY = selectedFeature.getSize().y();
        }

        this.MOUSE_SELECTION_MESH.setVertex(x, y, sizeX, sizeY);
        this.MOUSE_SELECTION_MESH.load();
    }

    /**
     * Sube a la <code>GPU</code> el terreno del mundo.
     */
    public void drawTerrain() {
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().load();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().load();
        }
        for (Entity.EntityType entityType: Entity.EntityType.values()) {
            entityType.getMesh().load();
        }
    }

    /**
     * Dibuja todos los elementos en pantalla.
     */
    @Override
    public void update(long dTime) {
        //Borramos los contenidos de la ventana y establecemos el color del fondo.
        GL20.glClearColor(38f / 255, 85f / 255, 144f /255, 0f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Activamos las transparencias
        GL20.glEnable(GL20.GL_BLEND);
        GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //Activamos el shader y subimos variables uniform al shader .glsl
        Shader.WORLD.use();
        Shader.WORLD.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
        Shader.WORLD.uploadMatrix4f("uView", CAMERA.getViewMatrix());
        Shader.WORLD.uploadFloat("uDaylight", (float) Main.WORLD.getDayLight());

        //Dibujamos el terreno.
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            Shader.WORLD.uploadInt("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);

            terrainType.getTexture().bind();
            terrainType.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos las features.
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            Shader.WORLD.uploadInt("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            featureType.getTexture().bind();

            featureType.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos las entidades.
        Shader.ENTITY.use();
        Shader.ENTITY.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
        Shader.ENTITY.uploadMatrix4f("uView", CAMERA.getViewMatrix());
        for (Entity.EntityType entityType: Entity.EntityType.values()) {
            Shader.ENTITY.uploadInt("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            entityType.getTexture().bind();

            if (Main.WORLD.getEntitiesMap().containsKey(entityType)) for (Entity entity: Main.WORLD.getEntitiesMap().get(entityType)) {
                Shader.ENTITY.upload2f("uInstancePosition", entity.getLocation().getX() * WorldScene.SPRITE_SIZE, entity.getLocation().getY() * WorldScene.SPRITE_SIZE);
                entityType.getMesh().draw();
            }
            Texture.unbind();
        }

        //Dibujamos el selector del ratón
        if (!MouseListener.inGameLocation.isOutOfTheWorld()) {
            Shader.WORLD.use();
            Shader.WORLD.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
            Shader.WORLD.uploadMatrix4f("uView", CAMERA.getViewMatrix());
            Shader.WORLD.uploadInt("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            WorldScene.MOUSE_TEXTURE.bind();
            this.MOUSE_SELECTION_MESH.draw();
            Texture.unbind();
        }

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        //Generamos la pantalla de debug
        if (Main.isDebugging) {
            Shader.HUD.upload2f("uHudPosition", 0, 0);
            Shader.HUD.upload2f("uHudSize", Window.getWidth() /2f, Window.getHeight());
            Shader.HUD.uploadInt("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);

            String debug = String.format("""
                            game:
                                fps=%d
                            
                            selection:
                                x=%.2f, y=%.2f
                            %s
                                            
                            camera:
                                x=%.2f, y=%.2f
                                zoom=%s
                            
                            world:
                                seed=%d
                                daytime=%d
                                features = %d, entities = %d
                            """,
                    (int) (1/ Time.nanosecondsToSeconds(dTime)),
                    MouseListener.inGameLocation.getX(),
                    MouseListener.inGameLocation.getY(),
                    MouseListener.inGameLocation.isOutOfTheWorld() ? "    OutOfTheWorld" : String.format("""
                                        terrain: %s type=%s
                                        %s
                                        biome=%s
                                        wordBuilder: c=%.4f,w=%.4f,r=%.4f
                                    """,
                            MouseListener.inGameLocation.getTerrain(),
                            MouseListener.inGameLocation.getTerrain().getType(),
                            MouseListener.inGameLocation.getFeature() == null ? "feature: null" : String.format("feature: %s type=%s",
                                    MouseListener.inGameLocation.getFeature(),
                                    MouseListener.inGameLocation.getFeature().getFeatureType()),
                            MouseListener.inGameLocation.getTerrain().getBiome(),
                            MouseListener.inGameLocation.getTerrain().getContinentalityNoise(),
                            MouseListener.inGameLocation.getTerrain().getWeirdnessNoise(),
                            MouseListener.inGameLocation.getTerrain().getRiversNoise()),
                    WorldScene.CAMERA.getCameraPosition().x(),
                    WorldScene.CAMERA.getCameraPosition().y(),
                    WorldScene.CAMERA.getZoom(),
                    Main.WORLD.getSeed(),
                    Main.WORLD.getDayTime(),
                    Main.WORLD.getFeaturesCount(),
                    Main.WORLD.getEntitiesCount());
            Graphics2dTexture texture = new Graphics2dTexture(Window.getWidth() / 2, Window.getHeight());
            Graphics2D graphics2D = texture.getGraphics();
            int posY = 10;
            for (String debugLine : debug.split("\n")) {
                graphics2D.drawString(debugLine, 0, posY);
                posY += graphics2D.getFontMetrics().getHeight();
            }

            texture.convert();
            texture.bind();
            this.HUD_MESH.draw();
            texture.remove();
            Texture.unbind();
        }
        this.INVENTORY.draw(this.HUD_MESH);

        Shader.detach();
    }

    @Override
    public void resizeWindow() {
        this.INVENTORY.setPixelSizeInScreen(Window.getHeight() / 205f);
    }

    @Override
    public void click(float mouseX, float mouseY) {
        this.INVENTORY.click(mouseX, mouseY);
    }
}
