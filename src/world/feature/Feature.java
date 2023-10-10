package world.feature;

import main.Main;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

public abstract class Feature implements Comparable<Feature> {
    private Location location;
    private Vector2i sizeInBlocks;
    private FeatureType featureType;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType) {
        this.location = location;
        this.sizeInBlocks = sizeInBlocks;
        this.featureType = featureType;
        if (this.canBePlaced()) {
            this.featureType.getMesh().addVertex((float) this.location.getX(), (float) this.location.getY(), this.sizeInBlocks.x(), this.sizeInBlocks.y());
            for (int x = 0; x < this.sizeInBlocks.x(); x++) for (int y = 0; y < this.sizeInBlocks.y(); y++) {
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

    public Vector2i getSize() {
        return new Vector2i(this.sizeInBlocks);
    }

    @Override
    public int hashCode() {
        return this.location.hashCode();
    }

    @Override
    public int compareTo(Feature feature) {
        int compareY = Double.compare(feature.getLocation().getY(), this.getLocation().getY());
        if (compareY == 0) {
            return Double.compare(feature.getLocation().getY(), this.getLocation().getY());
        }
        return compareY;
    }

    public enum FeatureType {
        TREE(new StaticTexture("assets/textures/feature/tree.png")),
        BUSH(new StaticTexture("assets/textures/feature/bush.png"));

        private WorldMesh mesh;
        private final Texture TEXTURE;

        FeatureType(Texture texture) {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), new int[]{2, 2});
            this.TEXTURE = texture;
        }

        public void updateMesh() {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), new int[]{2, 2});
            Main.WORLD.FEATURES_MAP.get(this).forEach(feature -> {
                mesh.addVertex((float) feature.getLocation().getX(), (float) feature.getLocation().getY(), feature.getSize().x(), feature.getSize().y());
            });
            this.mesh.load();
        }

        public WorldMesh getMesh() {
            return this.mesh;
        }

        public Texture getTexture() {
            return this.TEXTURE;
        }
    }
}
