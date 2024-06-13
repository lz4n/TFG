package game.world;

import game.Main;
import game.utils.Logger;

/**
 * Esta clase es responsable de generar el mundo del juego.
 * Contiene el método {@code run} que se encarga de inicializar y cargar el terreno en el mundo.
 */
public class WorldGenerator {

    /**
     * Genera el terreno del mundo del juego iterando todos los bloques. Llama al recolector de basura al terminar.
     */
    public void run() {
        for (int x = 0; x < Main.world.getSize(); x++) for (int y = Main.world.getSize() -1; y >=0; y--) {
            Main.world.getTerrain(x, y).addToMesh(x, y);
        }
        System.gc();
        Logger.sendMessage("Mundo generado correctamente.", Logger.LogMessageType.INFO);
    }
}
