package world.feature.building;

import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;

public class House extends Building {

    public House(Location location, int variant) {
        super(location, House.getHouseSize(variant +1), FeatureType.HOUSE, variant);
    }

    private static Vector2i getHouseSize(int level) {
        switch (level) {
            case 1:
                return new Vector2i(1, 1);
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return new Vector2i(2, 2);
            case 7:
                return new Vector2i(3, 3);
        }
        return new Vector2i(1, 1);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return true;
    }

    @Override
    public Vector2f getRandomOffset() {
        return new Vector2f(0, 0);
    }
}
