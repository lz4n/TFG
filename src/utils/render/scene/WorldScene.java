package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import ui.container.Inventory;
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
import world.particle.*;
import world.terrain.Terrain;

import java.awt.*;

/**
 * Escena encargada de renderizar el mundo.
 *
 * @author Izan
 */
public class WorldScene implements Scene {
    /**
     * Tamaño base (sin contar el zoom) de los sprites.
     */
    public static final int SPRITE_SIZE = 16;

    private final WorldMesh WORLD_BORDER_MESH = new WorldMesh(1, 2, 2);


    @Override
    public void init() {
        new WorldGenerator(Main.world, this).run();

        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.updateMesh();
            featureType.getMesh().adjust();
        }

        drawTerrain();

        SingleObjectMesh.SINGLE_OBJECT_MESH.load();

        this.WORLD_BORDER_MESH.addVertex(-1, -1, Main.world.getSize() +2,  Main.world.getSize() +2);
        this.WORLD_BORDER_MESH.load();
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
        Shader.WORLD.uploadFloat("uDaylight", Main.world.getDayLight());
        Shader.WORLD.uploadFloat("uHappiness", Main.world.getHappiness());
        Shader.WORLD.uploadInt("uIsPaused", Main.PLAYER.isPaused() ? 1 : 0);

        //Dibujamos el borde del mundo
        Shader.WORLD.uploadInt("customTextureUnit", 0);
        Shader.WORLD.uploadInt("textureSampler0", 0);
        Shader.WORLD.uploadInt("repeatingTimes", Main.world.getSize() +2);
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

        //Dibujamos las entidades y partículas.
        Shader.ENTITY.use();
        Shader.ENTITY.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
        Shader.ENTITY.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
        Shader.ENTITY.uploadFloat("uDaylight", Main.world.getDayLight());
        Shader.ENTITY.uploadFloat("uHappiness", Main.world.getHappiness());
        Shader.ENTITY.uploadFloat("uRotationAngle", 0);
        Shader.ENTITY.uploadFloat("uScale", 1);
        Shader.ENTITY.uploadInt("uIsPaused", Main.PLAYER.isPaused() ? 1: 0);

        for (Entity.EntityType entityType: Entity.EntityType.values()) {
            if (Main.world.getEntitiesMap().containsKey(entityType)) for (Entity entity: Main.world.getEntitiesMap().get(entityType)) {
                entityType.getTexture().draw(Shader.ENTITY, entity.getLocation());
            }
            Texture.unbind();
        }

        for (Particle particle: Main.world.getParticlesList()) {
            Shader.ENTITY.uploadFloat("uRotationAngle", particle.getRotation());
            Shader.ENTITY.uploadFloat("uScale", particle.getScale());
            particle.getTexture().draw(
                    Shader.ENTITY,
                    particle.getLocation()
            );
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
                                isPaused=%s
                                isHidingUI=%s
                                isUsingBulldozer=%s
                                isMouseOnInventory=%s
                                selection:
                                    x=%.2f, y=%.2f
                                    %scamera:
                                    x=%.2f, y=%.2f
                                    zoom=%s
                            
                            world:
                                seed=%d
                                dayTime=%d (%s) (dayLight=%.03f), day=%d, month=%d, years=%d
                                features = %d, entities = %d, particles = %d
                                
                                happiness=%.03f
                            """,
                    (int) (1/ Time.nanosecondsToSeconds(dTime)),
                    Main.tickSpeed,
                    Main.PLAYER.isPaused(),
                    Main.PLAYER.isHidingUi(),
                    Main.PLAYER.isUsingBulldozer(),
                    Main.PLAYER.isMouseOnInventory(),
                    MouseListener.getInGameLocation().getX(),
                    MouseListener.getInGameLocation().getY(),
                    MouseListener.getInGameLocation().isOutOfTheWorld() ? "    OutOfTheWorld" : String.format("""
                                            terrain: %s type=%s
                                                    %s
                                                    biome=%s
                                                    wordBuilder: c=%.4f,w=%.4f,r=%.4f
                                            """,
                            MouseListener.getInGameLocation().getTerrain(),
                            MouseListener.getInGameLocation().getTerrain().getType(),
                            MouseListener.getInGameLocation().getFeature() == null ? "feature: null" : String.format("feature: %s type=%s,variant=%d",
                                    MouseListener.getInGameLocation().getFeature(),
                                    MouseListener.getInGameLocation().getFeature().getFeatureType(),
                                    MouseListener.getInGameLocation().getFeature().getVariant()),
                            MouseListener.getInGameLocation().getTerrain().getBiome(),
                            MouseListener.getInGameLocation().getTerrain().getContinentalityNoise(),
                            MouseListener.getInGameLocation().getTerrain().getWeirdnessNoise(),
                            MouseListener.getInGameLocation().getTerrain().getRiversNoise()),
                    Main.PLAYER.getCamera().getCameraPosition().x(),
                    Main.PLAYER.getCamera().getCameraPosition().y(),
                    Main.PLAYER.getCamera().getZoom(),
                    Main.world.getSeed(),
                    Main.world.getDayTime(),
                    Main.world.getFormattedDayTime(),
                    Main.world.getDayLight(),
                    Main.world.getDay(),
                    Main.world.getMonth(),
                    Main.world.getYear(),
                    Main.world.getFeaturesCount(),
                    Main.world.getEntitiesCount(),
                    Main.world.getParticlesList().size(),
                    Main.world.getHappiness()
            );

            Graphics2dTexture debugScreen = GameFont.DEBUG.drawText(debug, Font.PLAIN, 12);

            Shader.HUD.upload2f("uPosition", 5, 5);
            Shader.HUD.upload2f("uSize", debugScreen.getSize().x(), debugScreen.getSize().y());
            Shader.HUD.uploadInt("texture_sampler", 0);

            debugScreen.bind();
            debugScreen.remove();

            Texture.unbind();
        }

        Main.PLAYER.updateDateTime(Main.world.getFormattedDayTime(), String.format("%d-%d-%d", Main.world.getDay(), Main.world.getMonth(), Main.world.getYear()));
        if (!Main.PLAYER.isHidingUi() || !(Main.PLAYER.getContainer() instanceof Inventory)) {
            Main.PLAYER.getContainer().onHoverEvent();
            Main.PLAYER.getContainer().draw();
        }

        //Dibujamos el selector del ratón
        if (!Main.PLAYER.isPaused() && MouseListener.getInGameLocation() != null && !MouseListener.getInGameLocation().isOutOfTheWorld() && !Main.PLAYER.isMouseOnInventory()) {
            Feature selectedFeature;
            if ((selectedFeature = MouseListener.getInGameLocation().getFeature()) != null) {
                for (int x = 1; x <= selectedFeature.getSize().x(); x++) for (int y = 1; y <= selectedFeature.getSize().y(); y++) {
                    Main.world.spawnParticle(new CursorParticle(selectedFeature.getLocation().add(x -1, y -1)));
                }
            } else {
                Main.world.spawnParticle(new CursorParticle(MouseListener.getInGameLocation()));
            }
        }

        Shader.detach();
    }

    @Override
    public void resizeWindow() {
        Main.PLAYER.getContainer().onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    @Override
    public void click(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi() && Main.PLAYER.isMouseOnInventory()) {
            Main.PLAYER.getContainer().onClickEvent(mouseX, mouseY);
        }
    }

    @Override
    public void moveMouse(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi()) {
            Main.PLAYER.setMouseOnInventory(Main.PLAYER.getContainer().onMouseMoveEvent(mouseX, mouseY));
        }
    }
}
