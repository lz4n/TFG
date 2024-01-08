package world.tick;

import java.util.LinkedList;
import java.util.List;

public class Ticking implements Tickable {
    private static List<Tickable> TICKABLE_OBJETS = new LinkedList<>();
    private static List<Tickable> FORCE_TICKABLE_OBJETS = new LinkedList<>();

    private Tickable onTick;

    public Ticking(Tickable onTick, boolean force) {
        this.onTick = onTick;
        if (force) {
            Ticking.FORCE_TICKABLE_OBJETS.add(this.onTick);
        } else {
            Ticking.TICKABLE_OBJETS.add(this.onTick);
        }
    }

    public Ticking() {
        this.onTick = this;
        if (this.getClass().isAnnotationPresent(ForceTicking.class)) {
            Ticking.FORCE_TICKABLE_OBJETS.add(this.onTick);
        } else {
            Ticking.TICKABLE_OBJETS.add(this.onTick);
        }
    }

    public void removeTicking() {
        Ticking.TICKABLE_OBJETS.remove(this);
        Ticking.FORCE_TICKABLE_OBJETS.remove(this);
    }

    @Override
    public void onTick(long deltaTime) {}

    public static void tick(long deltaTime) {
        new LinkedList<>(Ticking.TICKABLE_OBJETS).forEach(tickable -> {
            tickable.onTick(deltaTime);
        });
    }

    public static void tickForced(long deltaTime) {
        new LinkedList<>(Ticking.FORCE_TICKABLE_OBJETS).forEach(tickable -> {
            tickable.onTick(deltaTime);
        });
    }
}
