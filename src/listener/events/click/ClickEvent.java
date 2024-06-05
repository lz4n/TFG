package listener.events.click;

import listener.MouseListener;
import main.Main;
import org.lwjgl.glfw.GLFW;
import utils.KeyBind;
import utils.render.Window;
import world.entity.Duck;
import world.location.Location;

public class ClickEvent {
    public void event(Location screenLocation, int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (!Main.PLAYER.isMouseOnInventory() && !Main.PLAYER.isPaused()) {
                new ClickUIEvent();
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (Main.PLAYER.getTerrainSelection() != null) {
                Main.PLAYER.placeSelectedItem();
                Main.PLAYER.removeTerrainSelection();
            }

            Main.world.getDucks().forEach(duck -> duck.setSelected(false));

            for (Duck duck: Main.world.getDucks()) {
                if (duck.getBoundingBox().containsLocation(MouseListener.getInGameLocation())) {
                    duck.setSelected(true);

                    break;
                }
            }
        }
    }
}
