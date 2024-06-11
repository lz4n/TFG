package utils.render.mesh;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * Representa un mesh para la pantalla, que se utiliza para el shader de post-procesado que ocupa toda la pantalla.
 * Este mesh está compuesto por un cuadrado que cubre toda la pantalla y se usa para aplicar shaders de pantalla completa.
 * @see utils.render.Shader#POST_PROCESSING
 */
public class ScreenMesh extends Mesh {
    /**
     * Instancia estática del objeto ScreenMesh, que representa un único objeto mesh para la pantalla.
     * Este objeto mesh es utilizado para renderización u otras operaciones en el contexto de gráficos.
     */
    public static final ScreenMesh SCREEN_MESH = new ScreenMesh();

    private int screenVAO;

    /**
     * Constructor privado de ScreenMesh. Crea un nuevo ScreenMesh.
     * El ScreenMesh está compuesto por un cuadrado que cubre toda la pantalla.
     */
    private ScreenMesh() {
        super(4);

        this.vertexArray = new float[]{
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 0.0f,

                -1.0f,  1.0f,  0.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  1.0f, 1.0f
        };
    }

    /**
     * Carga los datos del mesh en la memoria de la GPU.
     * Genera los VAO y VBO necesarios y carga los datos de vértices del mesh.
     */
    @Override
    public void load() {
        FloatBuffer quadVerticesBuffer = MemoryUtil.memAllocFloat(this.vertexArray.length);
        quadVerticesBuffer.put(this.vertexArray).flip();

        this.screenVAO = GL30.glGenVertexArrays();
        int screenVBO = GL30.glGenBuffers();

        GL30.glBindVertexArray(this.screenVAO);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, screenVBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, quadVerticesBuffer, GL30.GL_STATIC_DRAW);

        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 0);

        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(quadVerticesBuffer);
    }

    /**
     * Obtiene el identificador del VAO del mesh de pantalla.
     *
     * @return El identificador del VAO del mesh de pantalla.
     */
    public int getScreenVAO() {
        return this.screenVAO;
    }
}
