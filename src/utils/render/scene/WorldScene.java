package utils.render.scene;

import listener.MouseListener;
import main.Main;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import utils.render.Window;
import utils.render.mesh.MaxSizedMesh;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.mesh.Mesh;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.StaticTexture;
import utils.render.mesh.MaxSizedRandomUVMesh;
import utils.render.mesh.SingleMesh;
import utils.render.texture.Texture;
import world.WorldGenerator;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;
import world.worldBuilder.Biome;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldScene extends Scene {
    public static final int SPRITE_SIZE = 20;

    public static Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.
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
            //Color
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 1f;
            this.vertexArray[previousVertexArrayLength++] = 1f;

            //Segundo vértice: arriba izquierda
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Color
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;

            //Tercer vértice: arriba derecha
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Color
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 1f;
            this.vertexArray[previousVertexArrayLength++] = 0f;

            //cuarto vértice: abajo izquierda
            //Posición
            this.vertexArray[previousVertexArrayLength++] = screenPosX;
            this.vertexArray[previousVertexArrayLength++] = screenPosY;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Color
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength++] = 0f;
            //Coordenadas UV
            this.vertexArray[previousVertexArrayLength++] = 0f;
            this.vertexArray[previousVertexArrayLength] = 1f;
        }
    };

    float[] vertexArray = {
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f
    };

    int[] elementArray = {
            0, 1, 2,
            2, 3, 0
    };
    private int vaoId2;


    public void init() {
        //Cargamos el shader
        Shader.DEFAULT_TEXTURE.compile();
        Shader.HUD.compile();
        new WorldGenerator(Main.WORLD, this).run();
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().adjust();
        }
        drawTerrain();

        //HUD
        int vboId, eboId;
        //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
        vaoId2 = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vaoId2);


        //Creamos el buffer de vértices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Creamos la VBO y la subimos al buffer
        vboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        //Creamos los índices
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

        //Añadimos los atributos a los vertices
        int positionsSize = 2;
        int vertexSizeBytes = (positionsSize) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        WorldScene.CAMERA.moveCamera(new Vector2f(Main.WORLD.getSize() / 2));
    }

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

        int vaoID = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vaoID);
        this.MOUSE_SELECTION_MESH.setVaoId(vaoID);

        //Creamos el buffer de vértices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(this.MOUSE_SELECTION_MESH.getVertexArray().length);
        vertexBuffer.put(this.MOUSE_SELECTION_MESH.getVertexArray()).flip();

        //Creamos la VBO y la subimos al buffer
        int vboID = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        //Creamos los índices
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(this.MOUSE_SELECTION_MESH.getElementArray().length);
        elementBuffer.put(this.MOUSE_SELECTION_MESH.getElementArray()).flip();

        int eboID = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

        //Añadimos los atributos a los vertices
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, colorSize, GL20.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);
    }

    public void drawTerrain() {
        int vaoId, vboId, eboId;
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
            vaoId = ARBVertexArrayObject.glGenVertexArrays();
            ARBVertexArrayObject.glBindVertexArray(vaoId);

            terrainType.getMesh().setVaoId(vaoId);

            //Creamos el buffer de vértices
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(terrainType.getMesh().getVertexArray().length);
            vertexBuffer.put(terrainType.getMesh().getVertexArray()).flip();

            //Creamos la VBO y la subimos al buffer
            vboId = GL20.glGenBuffers();
            GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
            GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

            //Creamos los índices
            IntBuffer elementBuffer = BufferUtils.createIntBuffer(terrainType.getMesh().getElementArray().length);
            elementBuffer.put(terrainType.getMesh().getElementArray()).flip();

            eboId = GL20.glGenBuffers();
            GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboId);
            GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

            //Añadimos los atributos a los vertices
            int positionsSize = 3;
            int colorSize = 4;
            int uvSize = 2;
            int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

            GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glEnableVertexAttribArray(0);

            GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
            vaoId = ARBVertexArrayObject.glGenVertexArrays();
            ARBVertexArrayObject.glBindVertexArray(vaoId);

            featureType.getMesh().setVaoId(vaoId);

            //Creamos el buffer de vértices
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(featureType.getMesh().getVertexArray().length);
            vertexBuffer.put(featureType.getMesh().getVertexArray()).flip();

            //Creamos la VBO y la subimos al buffer
            vboId = GL20.glGenBuffers();
            GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
            GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

            //Creamos los índices
            IntBuffer elementBuffer = BufferUtils.createIntBuffer(featureType.getMesh().getElementArray().length);
            elementBuffer.put(featureType.getMesh().getElementArray()).flip();

            eboId = GL20.glGenBuffers();
            GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboId);
            GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

            //Añadimos los atributos a los vertices
            int positionsSize = 3;
            int colorSize = 4;
            int uvSize = 2;
            int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

            GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glEnableVertexAttribArray(0);

            GL20.glVertexAttribPointer(1, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);
        }
    }

    public void update(long dTime) {
        Shader.DEFAULT_TEXTURE.use();

        //Subimos variables uniform al shader .glsl
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uView", CAMERA.getViewMatrix());
        Shader.DEFAULT_TEXTURE.uploadFloat("daylight", (float) Main.WORLD.getDayLight());

        GL20.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Activamos las transparencias
        GL20.glEnable(GL20.GL_BLEND);
        GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            Shader.DEFAULT_TEXTURE.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            terrainType.getMesh().getTexture().bind();

            ARBVertexArrayObject.glBindVertexArray(terrainType.getMesh().getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            GL20.glDrawElements(GL20.GL_TRIANGLES, terrainType.getMesh().getElementArray().length, GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            ARBVertexArrayObject.glBindVertexArray(0);
        }

        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            Shader.DEFAULT_TEXTURE.uploadTexture("texture_sampler", 0);
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            featureType.getMesh().getTexture().bind();

            ARBVertexArrayObject.glBindVertexArray(featureType.getMesh().getVaoId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            GL20.glDrawElements(GL20.GL_TRIANGLES, featureType.getMesh().getElementArray().length, GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            ARBVertexArrayObject.glBindVertexArray(0);
        }

        Shader.DEFAULT_TEXTURE.use();

        Shader.DEFAULT_TEXTURE.uploadTexture("texture_sampler", 0);

        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        this.MOUSE_SELECTION_MESH.getTexture().bind();
        ARBVertexArrayObject.glBindVertexArray(this.MOUSE_SELECTION_MESH.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glDrawElements(GL20.GL_TRIANGLES, this.MOUSE_SELECTION_MESH.getElementArray().length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ARBVertexArrayObject.glBindVertexArray(0);

        /*Shader.HUD.use();
        Shader.HUD.uploadMatrix4f("uProjection", new Matrix4f().ortho(0, Window.getWidth(), Window.getHeight(), 0, -1, 1));
        Shader.HUD.uploadMatrix4f("uView", new Matrix4f().identity());
        Shader.HUD.uploadTexture("texture_sampler", 0);
        Graphics2dTexture graphics2dTexture = new Graphics2dTexture(Window.getWidth(), Window.getHeight());
        Graphics2D graphics2D = graphics2dTexture.getGraphics();
        graphics2D .drawString("HOLA", 10, 10);//.fillRect(0, 0, 100, 100);

        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        graphics2dTexture.bind();
        ARBVertexArrayObject.glBindVertexArray(vaoId2);
        GL20.glEnableVertexAttribArray(0);
        GL20.glDrawElements(GL20.GL_TRIANGLES, elementArray.length, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        ARBVertexArrayObject.glBindVertexArray(0);*/

        Texture.unbind();
        Shader.detach();
    }
}
