package world.feature;

import main.Main;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

import java.util.Random;
import java.util.Set;

public abstract class Feature implements Comparable<Feature> {
    protected static final Random RANDOM = new Random();
    private final Location LOCATION;
    private final Vector2i SIZE_IN_BLOCKS;
    private final FeatureType FEATURE_TYPE;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType) {
        float offsetX = 0, offsetY = 0;
        if (this.getRandomOffset().x() != 0) {
            offsetX = Feature.RANDOM.nextFloat() / this.getRandomOffset().x();
        }
        if (this.getRandomOffset().y() != 0) {
            offsetY = Feature.RANDOM.nextFloat() / this.getRandomOffset().y();
        }
        this.LOCATION = location.add(offsetX, offsetY);
        this.SIZE_IN_BLOCKS = sizeInBlocks;
        this.FEATURE_TYPE = featureType;
    }

    public Location getLocation() {
        return this.LOCATION.clone();
    }

    public FeatureType getFeatureType() {
        return this.FEATURE_TYPE;
    }

    public Vector2i getSize() {
        return new Vector2i(this.SIZE_IN_BLOCKS);
    }

    public abstract Vector2i getRandomOffset();

    abstract public boolean canFeatureOverlapsWith(Feature feature);

    @Override
    public int hashCode() {
        return this.LOCATION.hashCode();
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
        BUSH(new StaticTexture("assets/textures/feature/bush.png")),
        TREE(new StaticTexture("assets/textures/feature/tree.png")),
        TREE2(new StaticTexture("assets/textures/feature/tree2.png"));

        private WorldMesh mesh;
        private final Texture TEXTURE;

        FeatureType(Texture texture) {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), 2, 2);
            this.TEXTURE = texture;
        }

        public void updateMesh() {
            Set<Feature> features = Main.WORLD.getFeaturesMap().get(this);
            this.mesh = new WorldMesh(features.size(), 2, 2);
            features.forEach(feature ->
                    mesh.addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getSize().x(), feature.getSize().y()));
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
