package game.utils.render.mesh;

/**
 * <code>Mesh</code> utilizado para renderizar objetos de forma individual, independientemente del tamaño o de si hay otros objetos.
 * @see Mesh
 *
 * @author Izan
 */
public class SingleObjectMesh extends Mesh {
    /**
     * Instancia estática del objeto SingleObjectMesh, que representa un único objeto mesh.
     * Este objeto mesh es utilizado para renderización u otras operaciones en el contexto de gráficos.
     */
    public static final SingleObjectMesh SINGLE_OBJECT_MESH = new SingleObjectMesh();

    private SingleObjectMesh() {
        super(2);
        this.vertexArray = new float[]{
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
        };
        this.elementArray = new int[]{
                0, 1, 2,
                2, 3, 0
        };
    }
}
