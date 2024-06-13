package game.utils.player.inventory;

import game.world.feature.Feature;
import game.world.location.Location;

import java.util.function.Function;

/**
 * Interfaz funcional que representa un colocador de características (features).
 * Extiende la interfaz {@code Function<Location, Feature>}, lo que significa que
 * proporciona un método para aplicar una función que toma una ubicación y devuelve una feature.
 */
public interface FeaturePlacer extends Function<Location, Feature> {
}
