package utils.render.scene;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import utils.Time;
import utils.render.Camera;
import utils.render.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class Scene {


    public abstract void init();


    public abstract void update(long dTime);
}
