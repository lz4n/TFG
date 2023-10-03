package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import utils.render.Window;
import utils.render.mesh.*;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.StaticTexture;
import world.WorldGenerator;
import world.feature.Feature;
import world.location.Location;
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
    public static final int SPRITE_SIZE = 20;

    /**
     * Camara de la escena.
     */
    public static Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    /**
     * <code>Mesh</code> anónimo utilizado para el selector del ratón.
     * @see Mesh
     */
    private final Mesh MOUSE_SELECTION_MESH = new Mesh(new StaticTexture("assets/textures/ui/selector.png")) {
        {
            this.vertexArray = new float[9*4];
            this.elementArray = new int[]{2, 1, 0, 0, 1, 3};
        }

        @Override
        public void addVertex(float x, float y, int sizeX, int sizeY) {
            float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;
            int previousVertexArrayLength = 0;

            //Primer vértice: abajo derecha
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 1f;
            this.vertexArray[previousVertexArrayLength++] = 1f;

            //Segundo vértice: arriba izquierda
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;

            //Tercer vértice: arriba derecha
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 1f;
            this.vertexArray[previousVertexArrayLength++] = 0f;

            //cuarto vértice: abajo izquierda
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength] = 1f;
        }
    }, HUD_MES = new HUDMesh(null);

    @Override
    public void init() {
        //Cargamos el shader
        Shader.TEXTURE.compile();
        Shader.HUD.compile();
        new WorldGenerator(Main.WORLD, this).run();
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().adjust();
        }
        drawTerrain();

        HUD_MES.load();
        int positionsSize = 2;
        int vertexSizeBytes = (positionsSize) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        WorldScene.CAMERA.moveCamera(new Vector2f(Main.WORLD.getSize() / 2));
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

        this.MOUSE_SELECTION_MESH.addVertex(x, y, sizeX, sizeY);
        this.MOUSE_SELECTION_MESH.load();
        //Añadimos los atributos a los vertices
        int positionsSize = 3;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + uvSize) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize* Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
    }

    /**
     * Sube a la <code>GPU</code> el terreno del mundo.
     */
    public void drawTerrain() {
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().load();
            //Añadimos los atributos a los vertices
            int positionsSize = 3;
            int uvSize = 2;
            int vertexSizeBytes = (positionsSize + uvSize) * Float.BYTES;

            GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glEnableVertexAttribArray(0);

            GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().load();
            //Añadimos los atributos a los vertices
            int positionsSize = 3;
            int uvSize = 2;
            int vertexSizeBytes = (positionsSize + uvSize) * Float.BYTES;

            GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glEnableVertexAttribArray(0);

            GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);
        }
    }

    /**
     * Dibuja todos los elementos en pantalla.
     */
    @Override
    public void update(long dTime) {
        Shader.TEXTURE.use();

        //Subimos variables uniform al shader .glsl
        Shader.TEXTURE.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
        Shader.TEXTURE.uploadMatrix4f("uView", CAMERA.getViewMatrix());
        Shader.TEXTURE.uploadFloat("daylight", (float) Main.WORLD.getDayLight());

        GL20.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Activamos las transparencias
        GL20.glEnable(GL20.GL_BLEND);
        GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            Shader.TEXTURE.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            terrainType.getMesh().getTexture().bind();

            ARBVertexArrayObject.glBindVertexArray(terrainType.getMesh().getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            GL20.glDrawElements(GL20.GL_TRIANGLES, terrainType.getMesh().getElementArray().length, GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            ARBVertexArrayObject.glBindVertexArray(0);
            ARBVertexArrayObject.glBindVertexArray(1);
            terrainType.getMesh().getTexture().unbind();
        }

        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            Shader.TEXTURE.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            featureType.getMesh().getTexture().bind();

            ARBVertexArrayObject.glBindVertexArray(featureType.getMesh().getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            GL20.glDrawElements(GL20.GL_TRIANGLES, featureType.getMesh().getElementArray().length, GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            ARBVertexArrayObject.glBindVertexArray(0);
            ARBVertexArrayObject.glBindVertexArray(1);
            featureType.getMesh().getTexture().unbind();
        }


        if (!MouseListener.inGameLocation.isOutOfTheWorld()) {
            Shader.TEXTURE.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            this.MOUSE_SELECTION_MESH.getTexture().bind();
            ARBVertexArrayObject.glBindVertexArray(this.MOUSE_SELECTION_MESH.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glDrawElements(GL20.GL_TRIANGLES, this.MOUSE_SELECTION_MESH.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            ARBVertexArrayObject.glBindVertexArray(0);
            this.MOUSE_SELECTION_MESH.getTexture().unbind();
        }

        if (Main.isDebugging) {
            Shader.HUD.use();
            Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
            Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());
            Shader.HUD.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            String debug = String.format("""
                            selection:
                                x=%.2f, y=%.2f
                            %s
                                            
                            camera:
                                x=%.2f, y=%.2f
                                zoom=%s
                            
                            world:
                                seed=%s
                                daytime=%s
                            """,
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
                    Main.WORLD.getDayTime());
            Graphics2dTexture texture = new Graphics2dTexture(900, 500);
            Graphics2D graphics2D = texture.getGraphics();
            int posY = 10;
            for (String debugLine : debug.split("\n")) {
                graphics2D.drawString(debugLine, 0, posY);
                posY += graphics2D.getFontMetrics().getHeight();
            }

            texture.bind();
            ARBVertexArrayObject.glBindVertexArray(this.HUD_MES.getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glDrawElements(GL20.GL_TRIANGLES, this.HUD_MES.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            ARBVertexArrayObject.glBindVertexArray(0);
            texture.unbind();
        }

        Shader.detach();
    }
}
