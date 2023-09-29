package utils.render.scene;

import main.Main;
import utils.render.mesh.MaxSizedMesh;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.texture.StaticTexture;
import utils.render.mesh.MaxSizedRandomUVMesh;
import utils.render.mesh.SingleMesh;
import world.WorldGenerator;
import world.feature.Feature;
import world.terrain.Terrain;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldScene extends Scene {
    public static final int SPRITE_SIZE = 20;

    public static Camera CAMERA = new Camera(new Vector2f()); //Iniciamos la cámara en 0,0.
    private final SingleMesh MOUSE_SELECTION_MESH = new SingleMesh(new StaticTexture("assets/textures/ui/selector.png"));

    public void init() {
        //Cargamos el shader
        Shader.DEFAULT_TEXTURE.compile();
        new WorldGenerator(Main.WORLD, this).run();
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            terrainType.getMesh().adjust();
        }
        for (Feature.FeatureType featureType: Feature.FeatureType.values()) {
            featureType.getMesh().adjust();
        }
        drawTerrain();
    }

    public void updateSelection(int x, int y) {
        System.err.println("a");
        System.out.println(Arrays.toString(this.MOUSE_SELECTION_MESH.getVertexArray()));

        this.MOUSE_SELECTION_MESH.setVertex(x, y);

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
        System.out.println(Main.WORLD.getDayTime() + " " + Main.WORLD.getDayLight());
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

        Shader.detach();
    }
}
