package utils.render;

import listener.KeyListener;
import listener.MouseListener;
import listener.WindowListener;
import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import utils.Time;
import utils.render.scene.Scene;
import utils.render.scene.WorldScene;

/**
 * Representa la ventana en la que se ejecuta el juego.
 *
 * @author Izan
 */
public class Window {
    /**
     * Tamaño por defecto de la ventana.
     */
    private final static int WIDTH = 1920, HEIGHT = 1080;

    /**
     * Identificador numérico de la ventana.
     */
    public static long window;

    /**
     * Escena actual de la ventana.
     */
    public static Scene currentScene = new WorldScene();

    /**
     * Inicia la ventana: inicializa sus componentes y genera el loop principal. Cuando el loop termina libera memoria
     * y desactiva GLSL.
     */
    public static void run() {
        init();
        currentScene.init();
        loop();

        //Liberamos memoria
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    /**
     * Inicializa los componentes gráficos y establece los atributos y comportamientos de la ventana.
     * También inicializa OpenGL: todos los comandos gráficos deben ejecutarse después de este método.
     */
    private static void init() {
        //Generamos el ErrorCallback
        GLFWErrorCallback.createPrint(System.err).set();

        //Inicializamos GLFW
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("No se ha podido inicializar GLFW");
        }

        //Congiguramos GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);

        //Creamos la ventana
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Test OpenGL", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("No se ha podido crear la ventana.");
        }

        //Generamos los eventos de teclado, ratón y pantalla
        GLFW.glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
        GLFW.glfwSetKeyCallback(window, KeyListener::keyCallback);
        GLFW.glfwSetWindowSizeCallback(window, WindowListener::windowCallback);

        //Establecemos OpenGL como el contexto actual
        GLFW.glfwMakeContextCurrent(window);

        //Activamos v-sync
        GLFW.glfwSwapInterval(1);

        //Hacemos la ventana visible
        GLFW.glfwShowWindow(window);

        //Creamos la instancia de OpenGL
        GL.createCapabilities();
    }

    /**
     * Crea el loop principal y calcula los FPS.
     */
    private static void loop() {
        long beginTime = Time.getTimeInNanoseconds(), endTime, dTime = -1;

        while (!GLFW.glfwWindowShouldClose(window)) {
            GLFW.glfwPollEvents();
            GLFW.glfwSwapBuffers(window);

            if (dTime >= 0) {
                Main.WORLD.onTick();
                Window.currentScene.update(dTime);
                GLFW.glfwSetWindowTitle(window, "EL PATO JUEGO");
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
                WorldScene.CAMERA.moveCamera(new Vector2f(0, 10 * (float) WorldScene.CAMERA.getZoom()));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
                WorldScene.CAMERA.moveCamera(new Vector2f(-10 * (float) WorldScene.CAMERA.getZoom(), 0));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
                WorldScene.CAMERA.moveCamera(new Vector2f(0, -10 * (float) WorldScene.CAMERA.getZoom()));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
                WorldScene.CAMERA.moveCamera(new Vector2f(10 * (float) WorldScene.CAMERA.getZoom(), 0));
            }

            endTime = Time.getTimeInNanoseconds();
            dTime = endTime - beginTime;
            beginTime = endTime;
        }
    }

    /**
     * @return Vector de dos enteros con las dimensiones de la ventana.
     *
     * @see Window#getWidth()
     * @see Window#getHeight()
     */
    private static Vector2i getDimensions() {
        try {
            int[] width = new int[1], height = new int[1];
            GLFW.glfwGetFramebufferSize(Window.window, width, height);
            return new Vector2i(width[0], height[0]);
        } catch (NullPointerException exception) {
            return new Vector2i(Window.WIDTH, Window.HEIGHT);
        }
    }

    /**
     * @return Ancho de la ventana.
     */
    public static int getWidth() {
        return Window.getDimensions().x();
    }

    /**
     * @return Alto de la ventana.
     */
    public static int getHeight() {
        return Window.getDimensions().y();
    }
}
