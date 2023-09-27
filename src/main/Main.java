package main;

import org.joml.Vector2d;
import utils.render.Window;
import utils.render.scene.WorldScene;
import world.World;
import world.entity.Player;

public class Main {
    //https://www.youtube.com/watch?v=88oZT7Aum6s&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=3

    public static final World WORLD = new World(1, 200);
    public static final Player PLAYER = new Player(new Vector2d(10, 10));

    public static void main(String[] args) {
        Window.run();
    }
}