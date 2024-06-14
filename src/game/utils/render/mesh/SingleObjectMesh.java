package game.utils.render.mesh;

/**
 * <code>Mesh</code> utilizado para renderizar objetos de forma individual, independientemente del tamaño o de si hay otros objetos.
 * @see Mesh
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
                0, 0,  // Vértice inferior izquierdo
                1, 0,  // Vértice inferior derecho
                0, 1,  // Vértice superior izquierdo
                1, 1   // Vértice superior derecho
        };
    }
}
