package utils.render;

import listener.KeyListener;
import listener.MouseListener;
import listener.WindowListener;
import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import ui.container.Frame;
import ui.container.Inventory;
import utils.KeyBind;
import utils.Logger;
import utils.Setting;
import utils.Time;
import utils.render.mesh.SingleObjectMesh;
import utils.render.scene.Scene;
import utils.render.scene.WorldScene;
import utils.render.texture.AnimatedTexture;
import utils.render.texture.CacheTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.entity.Duck;
import world.tick.Ticking;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

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
     * Tamaño inicial de la ventana.
     */
    private static int initialWidth, initialHeight;

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
        Window.init();
        currentScene.init();
        Main.PLAYER.init();

        //Cargamos las texturas a la caché de texuras
        Texture texture;
        for (Field field: Textures.class.getDeclaredFields()) {
            try {
                texture = (Texture) field.get(new Object());
                if (texture instanceof CacheTexture) {
                    ((CacheTexture) texture).init();
                    Logger.sendMessage("Se ha generado la textura %s: %s.", Logger.LogMessageType.INFO, texture.getTextureId(), texture);
                }
            } catch (IllegalAccessException | ClassCastException ignore) {}
        }

        Window.initialWidth = Window.getWidth();
        Window.initialHeight = Window.getHeight();

        Window.loop();

        //Guardamos las opciones
        Setting.save();

        //Liberamos memoria
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();

        for (Field field: Textures.class.getDeclaredFields()) {
            try {
                texture = (Texture) field.get(new Object());
                if (texture instanceof CacheTexture) {
                    ((CacheTexture) texture).remove();
                    Logger.sendMessage("Se ha eliminado la textura %s: %s.", Logger.LogMessageType.INFO, texture.getTextureId(), texture);
                }
            } catch (IllegalAccessException | ClassCastException exception) {}
        }
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
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        //Creamos la ventana
        window = GLFW.glfwCreateWindow(Window.WIDTH, Window.HEIGHT, "El pato juego", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("No se ha podido crear la ventana.");
        }

        GLFW.glfwMaximizeWindow(window);

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

        //Compilamos los shaders
        for (Shader shader: Shader.values()) {
            shader.compile();
        }

        //Establecemos el icono de la ventana
        try {
            BufferedImage bufferedImage = ImageIO.read(new java.io.File("assets/textures/icon.png"));
            byte[] iconData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            ByteBuffer appIconBuffer = BufferUtils.createByteBuffer(iconData.length);
            appIconBuffer.order(ByteOrder.nativeOrder());

            //Cambiamos los canales de la imagen, para que esté en formato RGBA en vez de ABGR.
            byte alpha, red, green, blue;
            for (int i = 0; i < iconData.length; i += 4) {
                alpha = iconData[i];
                blue = iconData[i + 1];
                green = iconData[i + 2];
                red = iconData[i + 3];
                appIconBuffer.put(red).put(green).put(blue).put(alpha);
            }
            appIconBuffer.flip();


            GLFWImage.Buffer glfwImage = GLFWImage.create(1);
            GLFWImage iconGI = GLFWImage.create().set(bufferedImage.getWidth(), bufferedImage.getHeight(), appIconBuffer);
            glfwImage.put(0, iconGI);
            GLFW.glfwSetWindowIcon(window, glfwImage);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //Si la opción FULLSCREEN está en true lo ponemos en pantalla completa.
        if (Setting.FULLSCREEN.getAsBoolean()) {
            Window.setFullScreen(true);
        }

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
                for (int tick = 0; tick < Main.tickSpeed; tick++) {
                    AnimatedTexture.animate();
                    if (!Main.PLAYER.isPaused()) {
                        Ticking.tick(dTime);
                    }
                }
                Ticking.tickForced(dTime);
                Window.currentScene.update(dTime);
            }

            if (!Main.PLAYER.isPaused()) {
                if (KeyBind.MOVE_UP.isPressed()) {
                    Main.PLAYER.getCamera().moveCamera(new Vector2f(0, (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * 10 * (float) Main.PLAYER.getCamera().getZoom()));
                }

                if (KeyBind.MOVE_LEFT.isPressed()) {
                    Main.PLAYER.getCamera().moveCamera(new Vector2f((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * -10 * (float) Main.PLAYER.getCamera().getZoom(), 0));
                }

                if (KeyBind.MOVE_DOWN.isPressed()) {
                    Main.PLAYER.getCamera().moveCamera(new Vector2f(0, (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * -10 * (float) Main.PLAYER.getCamera().getZoom()));
                }

                if (KeyBind.MOVE_RIGHT.isPressed()) {
                    Main.PLAYER.getCamera().moveCamera(new Vector2f((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * 10 * (float) Main.PLAYER.getCamera().getZoom(), 0));
                }

                if (KeyBind.DEBUG_SPAWN_ENTITY.isPressed()) {
                    Main.world.spawnEntity(new Duck(MouseListener.getInGameLocation()));
                }

                if (currentScene instanceof WorldScene && Main.PLAYER.getContainer() instanceof Inventory inventory) {
                    if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
                        inventory.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * -.7f);
                    }

                    if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
                        inventory.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * .7f);
                    }
                }
            } else {
                if (currentScene instanceof WorldScene && Main.PLAYER.getContainer() instanceof Frame frame) {
                    if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                        frame.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * -.7f);
                    }

                    if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_UP)) {
                        frame.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * .7f);
                    }
                }
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
            if (width[0] <= 0) {
                width[0] = Window.WIDTH;
            }
            if (height[0] <= 0) {
                height[0] = Window.HEIGHT;
            }
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

    /**
     * @return Ancho inicial de la ventana.
     */
    public static int getInitialWidth() {
        return Window.initialWidth;
    }

    /**
     * @return Alto inicial de la ventana.
     */
    public static int getInitialHeight() {
        return Window.initialHeight;
    }

    /**
     * Pone o quita la ventana en pantalla completa. Si deja de estar en pantalla completa se maximizará la pantalla.
     * @param fullScreen Si se va a poner a pantalla completa o no.
     */
    public static void setFullScreen(boolean fullScreen) {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(monitor);
        GLFW.glfwSetWindowMonitor(Window.window, fullScreen?monitor:MemoryUtil.NULL, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());

        if (!fullScreen) {
            GLFW.glfwMaximizeWindow(Window.window);
        }
    }

    /**
     * Alterna la pantalla completa.
     */
    public static void toggleFullscreen() {
        boolean fullScreen = !Setting.FULLSCREEN.getAsBoolean();
        setFullScreen(fullScreen);
        Setting.FULLSCREEN.setValue(fullScreen);
    }
}
