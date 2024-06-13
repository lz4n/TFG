package game.utils.render.scene;

import game.listener.MouseListener;
import game.Main;
import game.ui.container.Inventory;
import game.ui.tooltip.Tooltip;
import game.utils.Setting;
import game.utils.Time;
import game.utils.render.GameFont;
import game.utils.render.Shader;
import game.utils.render.Window;
import game.utils.render.mesh.ScreenMesh;
import game.utils.render.mesh.ShapeMesh;
import game.utils.render.mesh.SingleObjectMesh;
import game.utils.render.mesh.WorldMesh;
import game.utils.render.texture.Graphics2dTexture;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.world.particle.CursorParticle;
import game.world.particle.Particle;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import game.utils.render.mesh.*;
import org.lwjgl.opengl.*;

import game.utils.render.texture.*;
import game.world.WorldGenerator;
import game.world.entity.Duck;
import game.world.feature.Feature;
import game.world.feature.building.Building;
import game.world.location.Location;
import game.world.particle.*;
import game.world.terrain.Terrain;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ListIterator;

/**
 * Escena encargada de renderizar el mundo.
 *
 * @author Izan
 */
public class WorldScene {
    /**
     * Tamaño base (sin contar el zoom) de los sprites.
     */
    public static final int SPRITE_SIZE = 16;

    private static final int DEBUGGING_REFRESH_DELAY = 5;
    private final WorldMesh WORLD_BORDER_MESH = new WorldMesh(1, 2, 2);
    private int debuggingRefreshDelay = 30;
    private int textureColorbuffer, framebuffer;
    private Graphics2dTexture debugScreen;

    /**
     * Inicializa el mundo y carga los objetos necesarios para la representación gráfica.
     * Esto incluye la generación del mundo, ajuste de mallas de terrenos y características,
     * carga de objetos de malla únicos, y configuración del borde del mundo.
     * Se recomienda llamar a este método al inicio del programa para preparar el entorno gráfico.
     */
    public void init() {
        new WorldGenerator().run();

        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }

        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.updateMesh();
            featureType.getMesh().adjust();
        }

        this.drawTerrain();

        SingleObjectMesh.SINGLE_OBJECT_MESH.load();

        this.WORLD_BORDER_MESH.addVertex(-1, -1, Main.world.getSize() +2,  Main.world.getSize() +2);
        this.WORLD_BORDER_MESH.load();

        this.reloadPostProcessingBuffers();

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

    /**
     * Actualiza la escena del juego en cada fotograma.
     * Limpia el contenido de la ventana y establece el color de fondo.
     * Activa las transparencias y configura las propiedades de iluminación diurna si está activada.
     * Dibuja los elementos del mundo, entidades, partículas y objetos de depuración si se está depurando.
     * También gestiona la visualización de tooltips y la interacción del jugador con la interfaz de usuario.
     *
     * @param dTime Tiempo delta desde el último fotograma.
     */
    public void update(long dTime) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebuffer);

        //Borramos los contenidos de la ventana y establecemos el color del fondo.
        GL20.glClearColor(38f / 255, 85f / 255, 144f /255, 0f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.drawWorld();
        this.drawPostProcessing();
        this.drawDebugging(dTime);
        this.drawUI();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        Shader.detach();
    }

    private static void drawUI() {
        Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, game.utils.render.Window.getWidth(), game.utils.render.Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

        //Dibujamos el tooltip del pato seleccionado.
        for (Duck duck : Main.world.getDucks()) {
            if (duck.isSelected() && duck.getTooltip() != null) {
                duck.getTooltip().showAtWorld(duck.getLocation().add(duck.getBoundingBox().getSize()));
                break;
            }
        }

        //Dibujamos el inventario.
        Main.PLAYER.updateDateTime(Main.world.getFormattedDayTime(), String.format("%d-%d-%d", Main.world.getDay(), Main.world.getMonth(), Main.world.getYear()));
        if (!Main.PLAYER.isHidingUi() || !(Main.PLAYER.getContainer() instanceof Inventory)) {
            Main.PLAYER.getContainer().onHoverEvent();
            Main.PLAYER.getContainer().draw();
        }

        //Dibujamos el selector del ratón.
        if (!Main.PLAYER.isHidingUi() && !Main.PLAYER.isPaused() && MouseListener.getInGameLocation() != null && !MouseListener.getInGameLocation().isOutOfTheWorld() && !Main.PLAYER.isMouseOnInventory()) {
            Feature selectedFeature;
            if ((selectedFeature = MouseListener.getInGameLocation().getFeature()) != null && !Main.PLAYER.isSelectingTerrain()) {
                for (int x = 1; x <= selectedFeature.getBlockSize().x(); x++) for (int y = 1; y <= selectedFeature.getBlockSize().y(); y++) {
                    Main.world.addParticle(new CursorParticle(selectedFeature.getLocation().add(x -1, y -1)));
                }
            } else {
                Main.world.addParticle(new CursorParticle(MouseListener.getInGameLocation()));
            }

            if (selectedFeature instanceof Building building) {
                Tooltip tooltip = building.getTooltip();

                //Dibujamos el tooltip de la feature sobre la que tiene el ratón el jugador.
                if (tooltip != null) {
                    tooltip.showAtMouse();
                }
            }
        }

        //Dibujamos el tooltip del inventario del jugador.
        if (Main.PLAYER.getTooltip() != null) {
            Main.PLAYER.getTooltip().showAtMouse();
        }
    }

    private void drawDebugging(long dTime) {
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


            ShapeMesh entityPathMesh = new ShapeMesh(), walkedEntityPathMesh = new ShapeMesh(), destinationEntityPathMesh = new ShapeMesh();
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
            Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, game.utils.render.Window.getWidth(), game.utils.render.Window.getHeight(), 0, -1, 1));
            Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());

            debugScreen.draw(Shader.HUD, 5f, 5f, this.debugScreen.getSize().x(), debugScreen.getSize().y());

            Texture.unbind();
        }
    }

    private void drawPostProcessing() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        float daylight = Setting.DIURNAL_CYCLE.getValue() ? Main.world.getDayLight() : 1f;
        Shader.POST_PROCESSING.use();
        Shader.POST_PROCESSING.uploadInt("uScreenTexture", 0);
        Shader.POST_PROCESSING.uploadInt("uIsPaused", Main.PLAYER.isPaused() ? 1 : 0);
        Shader.POST_PROCESSING.uploadFloat("uDaylight", daylight);
        Shader.POST_PROCESSING.uploadFloat("uHappiness", Main.world.getHappiness());

        if (!MouseListener.getInGameLocation().isOutOfTheWorld() && daylight < 1) {
            Location mouseLocation = MouseListener.getInScreenLocation();
            Shader.POST_PROCESSING.upload2f("uLightPos", mouseLocation.getX() / game.utils.render.Window.getWidth(), 1 -mouseLocation.getY() / game.utils.render.Window.getHeight());

        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureColorbuffer);
        GL30.glBindVertexArray(ScreenMesh.SCREEN_MESH.getScreenVAO());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
    }

    private void drawWorld() {
        //Activamos el shader y subimos variables uniform al shader .glsl
        Shader.WORLD.use();
        Shader.WORLD.uploadMatrix4f("uProjection", Main.PLAYER.getCamera().getProjectionMatrix());
        Shader.WORLD.uploadMatrix4f("uView", Main.PLAYER.getCamera().getViewMatrix());
        for (int samplerId = 0; samplerId < Texture.SAMPLERS_COUNT; samplerId++) {
            Shader.WORLD.uploadInt("textureSamplers[" + samplerId + "]", samplerId);
        }

        //Dibujamos el borde del mundo
        Shader.WORLD.uploadInt("uCustomTextureUnit", 0);
        Shader.WORLD.uploadInt("uRepeatingTimes", Main.world.getSize() +2);
        Textures.WORLD_BORDER.bind();
        this.WORLD_BORDER_MESH.draw();
        Texture.unbind();

        Shader.WORLD.uploadInt("uRepeatingTimes", 1);

        //Dibujamos el terreno.
        Shader.WORLD.uploadInt("uCustomTextureUnit", 0);
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) if (!terrainType.getMesh().isEmpty()) {
            terrainType.getTexture().bind();
            terrainType.getMesh().draw();
            Texture.unbind();
        }

        //Dibujamos las features.
        Shader.WORLD.uploadInt("uCustomTextureUnit", -1);
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) if (!featureType.getMesh().isEmpty()) {
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
        Shader.ENTITY.uploadFloat("uRotationAngle", 0);
        Shader.ENTITY.uploadFloat("uScale", 1);
        Shader.ENTITY.uploadFloat("uTime", (float) GLFW.glfwGetTime());

        for (int samplerId = 0; samplerId < Texture.SAMPLERS_COUNT; samplerId++) {
            Shader.ENTITY.uploadInt("textureSamplers[" + samplerId + "]", samplerId);
        }

        for (Duck duck1 : Main.world.getDucks()) {
            Shader.ENTITY.uploadFloat("uFlip", duck1.getFlip());
            Shader.ENTITY.uploadInt("uIsSelected", duck1.isSelected() ? 1 : 0);
            duck1.getBodyTexture().draw(Shader.ENTITY, duck1.getLocation(), 0.8f);
            duck1.getOverlay().draw(Shader.ENTITY, duck1.getLocation(), 0.8f);
            Texture.unbind();
        }

        Shader.ENTITY.uploadFloat("uFlip", 0);
        Shader.ENTITY.uploadInt("uIsSelected", 0);

        for (Particle particle : Main.world.getParticlesList()) {
            Shader.ENTITY.uploadFloat("uRotationAngle", particle.getRotation());
            Shader.ENTITY.uploadFloat("uScale", particle.getScale());
            particle.getTexture().draw(
                    Shader.ENTITY,
                    particle.getLocation()
            );
            Texture.unbind();
        }
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

    /**
     * Recarga el buffer de pantalla y la textura dinámica de la pantalla, ambos utilizados para el shader de post-procesado.
     * @see Shader#POST_PROCESSING
     */
    public void reloadPostProcessingBuffers() {
        this.framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebuffer);

        // Crear la textura para el color buffer
        this.textureColorbuffer = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureColorbuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, game.utils.render.Window.getWidth(), game.utils.render.Window.getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureColorbuffer, 0);

        int rbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, game.utils.render.Window.getWidth(), game.utils.render.Window.getHeight());
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);

        ScreenMesh.SCREEN_MESH.load();
    }

    /**
     * Redimensiona la ventana del juego, ajustando el contenedor del jugador según las nuevas dimensiones.
     */
    public void resizeWindow() {
        Main.PLAYER.getContainer().onResizeWindowEvent(game.utils.render.Window.getWidth(), Window.getHeight());

        this.reloadPostProcessingBuffers();
    }

    /**
     * Maneja el evento de clic del mouse en la posición especificada.
     * Si la interfaz de usuario no está oculta y el mouse está sobre el inventario del jugador, se gestiona el evento de clic en el inventario.
     *
     * @param mouseX Posición X del mouse.
     * @param mouseY Posición Y del mouse.
     */
    public void click(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi() && Main.PLAYER.isMouseOnInventory()) {
            Main.PLAYER.getContainer().onClickEvent(mouseX, mouseY);
        }
    }

    /**
     * Maneja el movimiento del mouse en la posición especificada.
     * Si la interfaz de usuario no está oculta, actualiza el estado del mouse sobre el inventario del jugador basado en el evento de movimiento del mouse.
     *
     * @param mouseX Posición X del mouse.
     * @param mouseY Posición Y del mouse.
     */
    public void moveMouse(float mouseX, float mouseY) {
        if (!Main.PLAYER.isHidingUi()) {
            Main.PLAYER.setMouseOnInventory(Main.PLAYER.getContainer().onMouseMoveEvent(mouseX, mouseY));
        }
    }
}
