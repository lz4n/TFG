package utils.render.scene;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.TerrainMesh;
import utils.render.Texture;
import world.World;
import world.WorldGenerator;
import world.terrain.Terrain;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class WorldScene extends Scene {
    public static final int SPRITE_SIZE = 20;

    public final HashMap<Terrain.TerrainType, TerrainMesh> RENDER = new HashMap<>();
    public static Camera CAMERA = new Camera(new Vector2f()); //Iniciamos la cámara en 0,0.

    public WorldScene() {
        for (Terrain.TerrainType terrainType: Terrain.TerrainType.values()) {
            this.RENDER.put(terrainType, new TerrainMesh());
        }
    }

    public void init() {
        //Cargamos el shader
        Shader.DEFAULT_TEXTURE.compile();
        new WorldGenerator(Main.WORLD, this).run();
        drawTerrain();
    }

    public void drawTerrain() {
        this.RENDER.forEach((terrainType, render) -> {

            //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
            render.vaoID = ARBVertexArrayObject.glGenVertexArrays();
            ARBVertexArrayObject.glBindVertexArray(render.vaoID);

            //Creamos el buffer de vértices
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(render.vertexArray.length);
            vertexBuffer.put(render.vertexArray).flip();

            //Creamos la VBO y la subimos al buffer
            int vboID = GL20.glGenBuffers();
            GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
            GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

            //Creamos los índices
            IntBuffer elementBuffer = BufferUtils.createIntBuffer(render.elementArray.length);
            elementBuffer.put(render.elementArray).flip();

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
        });
}

    public void update(long dTime) {
        Shader.DEFAULT_TEXTURE.use();

        //Subimos variables uniform al shader .glsl
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uProjection", CAMERA.getProjectionMatrix());
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uView", CAMERA.getViewMatrix());

        GL20.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (Map.Entry<Terrain.TerrainType, TerrainMesh> entry: this.RENDER.entrySet()) {
            if (entry != null) {
                Shader.DEFAULT_TEXTURE.uploadTexture("texture_sampler", 0);
                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                Texture texture = new Texture(entry.getKey().getTexture());
                texture.bind();

                ARBVertexArrayObject.glBindVertexArray(entry.getValue().vaoID);
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);

                GL20.glDrawElements(GL20.GL_TRIANGLES, entry.getValue().elementArray.length, GL11.GL_UNSIGNED_INT, 0);
                Texture.unbind();

                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);

                ARBVertexArrayObject.glBindVertexArray(0);
            }
        }
        Shader.detach();
    }
}
