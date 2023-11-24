package world.tick;

import java.util.LinkedList;
import java.util.List;

public class Ticking implements Tickable {
    private static List<Tickable> TICKABLE_OBJETS = new LinkedList<>();

    private Tickable onTick;

    public Ticking(Tickable onTick) {
        this.onTick = onTick;
        Ticking.TICKABLE_OBJETS.add(this.onTick);
    }

    public Ticking() {
        this.onTick = this;
        Ticking.TICKABLE_OBJETS.add(this.onTick);
    }

    public void removeTicking() {
        Ticking.TICKABLE_OBJETS.remove(this);
    }

    @Override
    public void onTick(long deltaTime) {}

    public static List<Tickable> getTickableObjets() {
        return new LinkedList<>(Ticking.TICKABLE_OBJETS);
    }
}
