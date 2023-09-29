package world.feature;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2i;
import utils.render.mesh.MaxSizedMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

public abstract class Feature {
    private Location location;
    private Vector2i sizeInBlocks;
    private FeatureType featureType;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType) {
        this.location = location;
        this.sizeInBlocks = sizeInBlocks;
        this.featureType = featureType;
        if (this.canBePlaced()) {
            this.featureType.getMesh().addVertex((float) this.location.getX(), (float) this.location.getY(), this.sizeInBlocks.x(), this.sizeInBlocks.y());
            for (int x = 0; x <= this.sizeInBlocks.x(); x++) for (int y = 0; y <= this.sizeInBlocks.y(); y++) {
                Main.WORLD.setFeature((int) this.location.getX() + x, (int) this.location.getY() + y, this);
            }
        }
    }

    private boolean canBePlaced() {
        Feature feature;
        for (int x = 0; x <= this.sizeInBlocks.x(); x++) for (int y = 0; y <= this.sizeInBlocks.y(); y++) {
            feature = this.getLocation().add(x, y).getFeature();
            if (feature != null && !feature.getFeatureType().equals(this.featureType)) return false;

        }
        return true;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public enum FeatureType {
        TREE(new StaticTexture("assets/textures/feature/tree.png")),
        BUSH(new StaticTexture("assets/textures/feature/bush.png"));

        private final MaxSizedMesh MESH;

        FeatureType(Texture texture) {
            this.MESH = new MaxSizedMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), texture);
        }

        public MaxSizedMesh getMesh() {
            return this.MESH;
        }
    }
}
