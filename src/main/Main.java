package main;

import utils.Player;
import utils.render.Window;
import world.World;

import java.util.Random;

public class Main {
    //https://www.youtube.com/watch?v=88oZT7Aum6s&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=3

    public static final World WORLD = new World(new Random().nextInt(100000000), 500);
    public static int tickSpeed = 1;
    public static final Player PLAYER = new Player();
    public static final Random RANDOM = new Random();

    /**
     * Si el juego está en modo debug o no.
     */
    public static boolean isDebugging = false;

    public static void main(String[] args) {
        for (String argument: args) {
            if (argument.equals("--activeDebug")) {
                isDebugging = true;
                break;
            }
        }

        Window.run();
    }


}