package utils.player.inventory;

import world.feature.Feature;
import world.location.Location;

import java.util.function.Function;

public interface FeaturePlacer extends Function<Location, Feature> {
}
