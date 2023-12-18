package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import utils.Time;
import utils.render.GameFont;
import utils.render.Window;
import utils.render.mesh.*;
import org.lwjgl.opengl.*;
import utils.render.Shader;
import utils.render.texture.*;
import world.WorldGenerator;
import world.entity.Entity;
import world.feature.Feature;
import world.location.Location;
import world.particle.*;
import world.terrain.Terrain;

import java.awt.*;

/**
 * Escena encargada de renderizar el mundo.
 *
 * @author Izan
 */
public class WorldScene extends Scene {
    /**
     * Tamaño base (sin contar el zoom) de los sprites.
     */
    public static final int SPRITE_SIZE = 16;

    /**
     * <code>Mesh</code> utilizado para el selector del ratón.
     * @see Mesh
     */
    private final MouseSelectionMesh MOUSE_SELECTION_MESH = new MouseSelectionMesh(3, 2);

    /**
     * Mesh utilizado para dibujar el HUD.
     */
    private final HUDMesh HUD_MESH = new HUDMesh();

    private final WorldMesh WORLD_BORDER_MESH = new WorldMesh(1, 2, 2);


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

        this.WORLD_BORDER_MESH.addVertex(-1, -1, Main.WORLD.getSize() +2,  Main.WORLD.getSize() +2);
        this.WORLD_BORDER_MESH.load();
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

        new BulldozerParticle(new Location(0, 0)).getMesh().load();
        new NegativeParticle(new Location(0, 0)).getMesh().load();
        new PositiveParticle(new Location(0, 0)).getMesh().load();
        new CloudParticle(new Location(0, 0), 0, true).getMesh().load();
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
        Shader.WORLD.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
        Shader.WORLD.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
        Shader.WORLD.uploadFloat("uDaylight", Main.WORLD.getDayLight());
        Shader.WORLD.uploadInt("uSeason", Main.WORLD.getSeason());
        Shader.WORLD.uploadFloat("uHappiness", Main.WORLD.getHappiness());

        //Dibujamos el borde del mundo
        Shader.WORLD.uploadInt("customTextureUnit", 0);
        Shader.WORLD.uploadInt("textureSampler0", 0);
        Shader.WORLD.uploadInt("repeatingTimes", Main.WORLD.getSize() +2);
        Textures.WORLD_BORDER.bind();
        this.WORLD_BORDER_MESH.draw();
        Texture.unbind();
        Shader.WORLD.uploadInt("repeatingTimes", 1);

        //Dibujamos el terreno.
        Shader.WORLD.uploadInt("customTextureUnit", 0);
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            Shader.WORLD.uploadInt("textureSampler0", 0);
            terrainType.getTexture().bind();
            terrainType.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos las features.
        Shader.WORLD.uploadInt("customTextureUnit", -1);
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            Shader.WORLD.uploadInt("textureSampler0", 0);
            Shader.WORLD.uploadInt("textureSampler1", 1);
            Shader.WORLD.uploadInt("textureSampler2", 2);
            Shader.WORLD.uploadInt("textureSampler3", 3);
            Shader.WORLD.uploadInt("textureSampler4", 4);
            for (int variant = 0; variant < featureType.getVariants(); variant++) {
                featureType.getTextures().get(variant).bind(variant);
            }

            featureType.getMesh().draw();

            Texture.unbind();
        }

        //Dibujamos las entidades y features.
        Shader.ENTITY.use();
        Shader.ENTITY.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
        Shader.ENTITY.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
        Shader.ENTITY.uploadFloat("uDaylight", Main.WORLD.getDayLight());
        Shader.ENTITY.uploadInt("uSeason", Main.WORLD.getSeason());
        Shader.ENTITY.uploadFloat("uHappiness", Main.WORLD.getHappiness());

        for (Entity.EntityType entityType: Entity.EntityType.values()) {
            entityType.getTexture().bind();

            if (Main.WORLD.getEntitiesMap().containsKey(entityType)) for (Entity entity: Main.WORLD.getEntitiesMap().get(entityType)) {
                Shader.ENTITY.upload2f("uInstancePosition", entity.getLocation().getX() * WorldScene.SPRITE_SIZE, entity.getLocation().getY() * WorldScene.SPRITE_SIZE);
                entityType.getMesh().draw();
            }
            Texture.unbind();
        }

        for (Particle particle: Main.WORLD.getParticlesList()) {
            particle.getTexture().bind();
            Shader.ENTITY.upload2f("uInstancePosition", particle.getLocation().getX() *WorldScene.SPRITE_SIZE, particle.getLocation().getY() * WorldScene.SPRITE_SIZE);
            Shader.ENTITY.uploadFloat("uRotationAngle", particle.getRotation());
            Shader.ENTITY.uploadFloat("uScale", particle.getScale());
            particle.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos el selector del ratón
        if (MouseListener.inGameLocation != null && !MouseListener.inGameLocation.isOutOfTheWorld()) {
            Shader.WORLD.use();
            Shader.WORLD.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
            Shader.WORLD.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
            Textures.SELECTOR.bind();
            this.MOUSE_SELECTION_MESH.draw();
            Texture.unbind();
        }

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        //Generamos la pantalla de debug
        if (Main.isDebugging) {
            String debug = String.format("""
                            game:
                                fps=%d
                                tickSpeed=%d
                            
                            player:
                                isHidingUI=%s
                                isUsingBulldozer=%s
                                selection:
                                    x=%.2f, y=%.2f
                                    %scamera:
                                    x=%.2f, y=%.2f
                                    zoom=%s
                            
                            world:
                                seed=%d
                                dayTime=%d (%s) (dayLight=%.03f), day=%d (season=%d), years=%d
                                features = %d, entities = %d, particles = %d
                                
                                happiness=%.03f
                            """,
                    (int) (1/ Time.nanosecondsToSeconds(dTime)),
                    Main.tickSpeed,
                    Main.PLAYER.isHidingUi(),
                    Main.PLAYER.isUsingBulldozer(),
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
                            MouseListener.inGameLocation.getFeature() == null ? "feature: null" : String.format("feature: %s type=%s,variant=%d",
                                    MouseListener.inGameLocation.getFeature(),
                                    MouseListener.inGameLocation.getFeature().getFeatureType(),
                                    MouseListener.inGameLocation.getFeature().getVariant()),
                            MouseListener.inGameLocation.getTerrain().getBiome(),
                            MouseListener.inGameLocation.getTerrain().getContinentalityNoise(),
                            MouseListener.inGameLocation.getTerrain().getWeirdnessNoise(),
                            MouseListener.inGameLocation.getTerrain().getRiversNoise()),
                    Main.PLAYER.getCamera().getCameraPosition().x(),
                    Main.PLAYER.getCamera().getCameraPosition().y(),
                    Main.PLAYER.getCamera().getZoom(),
                    Main.WORLD.getSeed(),
                    Main.WORLD.getDayTime(),
                    Main.WORLD.getFormattedDayTime(),
                    Main.WORLD.getDayLight(),
                    Main.WORLD.getDay(),
                    Main.WORLD.getSeason(),
                    Main.WORLD.getYear(),
                    Main.WORLD.getFeaturesCount(),
                    Main.WORLD.getEntitiesCount(),
                    Main.WORLD.getParticlesList().size(),
                    Main.WORLD.getHappiness()
            );

            Graphics2dTexture debugScreen = GameFont.DEBUG.drawText(debug, Font.PLAIN, 12);

            Shader.HUD.upload2f("uHudPosition", 5, 5);
            Shader.HUD.upload2f("uHudSize", debugScreen.getSize().x(), debugScreen.getSize().y());
            Shader.HUD.uploadInt("texture_sampler", 0);

            debugScreen.bind();
            this.HUD_MESH.draw();
            debugScreen.remove();
            Texture.unbind();
        }

        Main.PLAYER.updateTime(Main.WORLD.getFormattedDayTime());
        if (!Main.PLAYER.isHidingUi()) {
            Main.PLAYER.getInventory().onHoverEvent();
            Main.PLAYER.getInventory().draw(this.HUD_MESH);
        }

        Shader.detach();
    }

    @Override
    public void resizeWindow() {
        Main.PLAYER.getInventory().onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    @Override
    public void click(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi()) {
            Main.PLAYER.getInventory().onClickEvent(mouseX, mouseY);
        }
    }

    @Override
    public void moveMouse(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi()) {
            Main.PLAYER.getInventory().onMouseMoveEvent(mouseX, mouseY);
        }
    }
}
