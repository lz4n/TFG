package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ui.container.Inventory;
import utils.Setting;
import utils.Time;
import utils.render.GameFont;
import utils.render.Window;
import utils.render.mesh.*;
import org.lwjgl.opengl.*;
import utils.render.Shader;
import utils.render.texture.*;
import world.WorldGenerator;
import world.entity.Duck;
import world.feature.Feature;
import world.location.Location;
import world.particle.*;
import world.terrain.Terrain;
import world.worldBuilder.Biome;

import java.awt.*;
import java.util.ListIterator;

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

    private static final int DEBUGGING_REFRESH_DELAY = 5;

    private final WorldMesh WORLD_BORDER_MESH = new WorldMesh(1, 2, 2);

    private int debuggingRefreshDelay = 30;
    private Graphics2dTexture debugScreen;

    @Override
    public void init() {
        new WorldGenerator().run();

        Main.world.setTerrain(0, 0, new Terrain(Terrain.TerrainType.STONE_TILE, Biome.BEACH, 0, 0, 0));

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

        System.gc();
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


    public void update(long dTime) {
        //Borramos los contenidos de la ventana y establecemos el color del fondo.
        GL20.glClearColor(38f / 255, 85f / 255, 144f /255, 0f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Activamos las transparencias
        GL20.glEnable(GL20.GL_BLEND);
        GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float dayLight = Setting.DIURNAL_CYCLE.getValue() ? Main.world.getDayLight() : 1f;

        //Activamos el shader y subimos variables uniform al shader .glsl
        Shader.WORLD.use();
        Shader.WORLD.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
        Shader.WORLD.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
        Shader.WORLD.uploadFloat("uDaylight", dayLight);
        Shader.WORLD.uploadFloat("uHappiness", Main.world.getHappiness());
        Shader.WORLD.uploadInt("uIsPaused", Main.PLAYER.isPaused() ? 1 : 0);
        for (int samplerId = 0; samplerId < Texture.SAMPLERS_COUNT; samplerId++) {
            Shader.WORLD.uploadInt("textureSamplers[" + samplerId + "]", samplerId);
        }

        //Dibujamos el borde del mundo
        Shader.WORLD.uploadInt("customTextureUnit", 0);
        Shader.WORLD.uploadInt("repeatingTimes", Main.world.getSize() +2);
        Textures.WORLD_BORDER.bind();
        this.WORLD_BORDER_MESH.draw();
        Texture.unbind();
        Shader.WORLD.uploadInt("repeatingTimes", 1);

        //Dibujamos el terreno.
        Shader.WORLD.uploadInt("customTextureUnit", 0);
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getTexture().bind();
            terrainType.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos las features.
        Shader.WORLD.uploadInt("customTextureUnit", -1);
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
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
        Shader.ENTITY.uploadFloat("uDaylight", dayLight);
        Shader.ENTITY.uploadFloat("uHappiness", Main.world.getHappiness());
        Shader.ENTITY.uploadFloat("uRotationAngle", 0);
        Shader.ENTITY.uploadFloat("uScale", 1);
        Shader.ENTITY.uploadFloat("uTime", (float) GLFW.glfwGetTime());
        Shader.ENTITY.uploadInt("uIsPaused", Main.PLAYER.isPaused() ? 1: 0);

        for (int samplerId = 0; samplerId < Texture.SAMPLERS_COUNT; samplerId++) {
            Shader.ENTITY.uploadInt("textureSamplers[" + samplerId + "]", samplerId);
        }

        Main.world.getDucks().forEach(duck -> {
            Shader.ENTITY.uploadFloat("uFlip", duck.getFlip());
            Shader.ENTITY.uploadInt("uIsSelected", duck.isSelected() ? 1 : 0);
            duck.getTexture().draw(Shader.ENTITY, duck.getLocation(), 0.8f);
            Texture.unbind();
        });


        Shader.ENTITY.uploadInt("uIsSelected", 0);
        Main.world.getParticlesList().forEach(particle -> {
            Shader.ENTITY.uploadFloat("uRotationAngle", particle.getRotation());
            Shader.ENTITY.uploadFloat("uScale", particle.getScale());
            if (particle instanceof CursorParticle) {
                System.out.println(particle.getLocation());
            }
            particle.getTexture().draw(
                    Shader.ENTITY,
                    particle.getLocation()
            );
            Texture.unbind();
        });

        //Generamos la pantalla de debug
        if (Main.isDebugging) {

            if (this.debuggingRefreshDelay++ >= WorldScene.DEBUGGING_REFRESH_DELAY) {
                this.debuggingRefreshDelay = 0;
                if (this.debugScreen != null) {
                    this.debugScreen.remove();
                }
                this.debugScreen = generateDebugScreen(dTime);
            }

            Shader.SHAPE.use();
            Shader.SHAPE.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
            Shader.SHAPE.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
            Shader.SHAPE.upload2f("uPosition", 0, 0);


            TooltipMesh entityPathMesh = new TooltipMesh(), walkedEntityPathMesh = new TooltipMesh(), destinationEntityPathMesh = new TooltipMesh();
            ListIterator<Location> entityPath;
            Location pathLocation;

            for (Duck duck : Main.world.getDucks().stream().filter(Duck::isSelected).toList()) {
                entityPath = duck.getPathfinding();

                while (entityPath.hasNext()) {
                    pathLocation = entityPath.next().clone().add(0, 1);

                    if (entityPath.previousIndex() >= duck.getPathIndex()) {
                        if (entityPath.hasNext()) {
                            entityPathMesh.addVertex(pathLocation.getX() +.2f, pathLocation.getY() -.8f, .6f, .6f, SPRITE_SIZE);
                        } else {
                            destinationEntityPathMesh.addVertex(pathLocation.getX() +.1f, pathLocation.getY() -.9f, .8f, .8f, SPRITE_SIZE);
                        }
                    } else {
                        walkedEntityPathMesh.addVertex(pathLocation.getX() +.3f, pathLocation.getY() -.7f, .4f, .4f, SPRITE_SIZE);
                    }
                }
            }

            Shader.SHAPE.upload4f("uColor", 1f, 0f, 0f, 0.5f);
            entityPathMesh.load();
            entityPathMesh.draw();
            Shader.SHAPE.upload4f("uColor", 0f, 1f, 0f, 0.5f);
            walkedEntityPathMesh.load();
            walkedEntityPathMesh.draw();
            Shader.SHAPE.upload4f("uColor", 0f, 0f, 1f, 0.7f);
            destinationEntityPathMesh.load();
            destinationEntityPathMesh.draw();

            Shader.HUD.use();
            Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
            Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

            debugScreen.draw(Shader.HUD, 5f, 5f, this.debugScreen.getSize().x(), debugScreen.getSize().y());

            Texture.unbind();
        }

        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        for (Duck duck : Main.world.getDucks()) {
            if (duck.isSelected() && duck.getTooltip() != null) {
                duck.getTooltip().showAtWorld(duck.getLocation().add(duck.getBoundingBox().getSize()));
                break;
            }
        }

        Main.PLAYER.updateDateTime(Main.world.getFormattedDayTime(), String.format("%d-%d-%d", Main.world.getDay(), Main.world.getMonth(), Main.world.getYear()));
        if (!Main.PLAYER.isHidingUi() || !(Main.PLAYER.getContainer() instanceof Inventory)) {
            Main.PLAYER.getContainer().onHoverEvent();
            Main.PLAYER.getContainer().draw();
        }

        //Dibujamos el selector del ratón
        if (!Main.PLAYER.isHidingUi() && !Main.PLAYER.isPaused() && MouseListener.getInGameLocation() != null && !MouseListener.getInGameLocation().isOutOfTheWorld() && !Main.PLAYER.isMouseOnInventory()) {
            Feature selectedFeature;
            if ((selectedFeature = MouseListener.getInGameLocation().getFeature()) != null && !Main.PLAYER.isSelectingTerrain()) {
                for (int x = 1; x <= selectedFeature.getSize().x(); x++) for (int y = 1; y <= selectedFeature.getSize().y(); y++) {
                    Main.world.addParticle(new CursorParticle(selectedFeature.getLocation().add(x -1, y -1)));
                }
            } else {
                Main.world.addParticle(new CursorParticle(MouseListener.getInGameLocation()));
            }
        }

        if (Main.PLAYER.getTooltip() != null) {
            Main.PLAYER.getTooltip().showAtMouse();
        }

        Shader.detach();
    }

    private static Graphics2dTexture generateDebugScreen(long dTime) {
        Runtime runtime = Runtime.getRuntime();
        String debug = String.format("""
                        game:
                            fps=%d
                            memory used=%dMb (%.02f%%) (F10 to call G.C.)
                            availableProcessors=%d
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
                            features = %d, ducks = %d, particles = %d
                            
                            happiness=%.03f
                            unemployment=%.03f
                        """,
                (int) (1/ Time.nanosecondsToSeconds(dTime)),
                (runtime.totalMemory() -runtime.freeMemory()) /1048576,
                ((float) runtime.freeMemory() /runtime.totalMemory()) *100,
                runtime.availableProcessors(),
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
                Main.world.getDucksCount(),
                Main.world.getParticlesList().size(),
                Main.world.getHappiness(),
                Main.world.getUnemployment()
        );

        Graphics2dTexture debugScreen = GameFont.DEBUG.drawText(debug, Font.PLAIN, 12);
        return debugScreen;
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
