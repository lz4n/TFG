package utils.render;

import listener.KeyListener;
import listener.MouseListener;
import main.Main;
import org.joml.Vector2d;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import utils.Time;
import utils.render.scene.Scene;
import utils.render.scene.WorldScene;

public class Window {
    private final static int WIDTH = 1920, HEIGHT = 1080;
    public static long window;
    public static Scene currentScene = new WorldScene();

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

    public static void setScene(Scene scene) {
        Window.currentScene = scene;
    }

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

        GLFW.glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
        GLFW.glfwSetKeyCallback(window, KeyListener::keyCallback);

        //Establecemos OpenGL como el contexto actual
        GLFW.glfwMakeContextCurrent(window);

        //Activamos v-sync
        GLFW.glfwSwapInterval(1);

        //Hacemos la ventana visible
        GLFW.glfwShowWindow(window);

        //Creamos la instancia de OpenGL
        GL.createCapabilities();
    }

    private static void loop() {
        int tick = 0;
        long beginTime = Time.getTimeInNanoseconds(), endTime, dTime = -1;

        while (!GLFW.glfwWindowShouldClose(window)) {
            GLFW.glfwPollEvents();
            GLFW.glfwSwapBuffers(window);

            if (dTime >= 0) {
                Window.currentScene.update(dTime);
                GLFW.glfwSetWindowTitle(window, "FPS: " + (int) (1/Time.nanosecondsToSeconds(dTime)));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
                Main.PLAYER.move(new Vector2d(0, 1));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
                Main.PLAYER.move(new Vector2d(-1, 0));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
                Main.PLAYER.move(new Vector2d(0, -1));
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
                Main.PLAYER.move(new Vector2d(1, 0));
            }

            endTime = Time.getTimeInNanoseconds();
            dTime = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth() {
        return Window.WIDTH;
    }

    public static int getHeight() {
        return Window.HEIGHT;
    }
}
