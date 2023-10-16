package world.feature;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

import java.util.Random;

public abstract class Feature implements Comparable<Feature> {
    protected static final Random RANDOM = new Random();
    private Location location;
    private Vector2i sizeInBlocks;
    private FeatureType featureType;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType) {
        float offsetX = 0, offsetY = 0;
        if (this.getRandomOffset().x() != 0) {
            offsetX = Feature.RANDOM.nextFloat() / this.getRandomOffset().x();
        }
        if (this.getRandomOffset().y() != 0) {
            offsetY = Feature.RANDOM.nextFloat() / this.getRandomOffset().y();
        }
        this.location = location.add(offsetX, offsetY);
        this.sizeInBlocks = sizeInBlocks;
        this.featureType = featureType;
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

    public abstract Vector2i getRandomOffset();

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
        BUSH(new StaticTexture("assets/textures/feature/bush.png")),
        TREE(new StaticTexture("assets/textures/feature/tree.png"));

        private WorldMesh mesh;
        private final Texture TEXTURE;

        FeatureType(Texture texture) {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), new int[]{2, 2});
            this.TEXTURE = texture;
        }

        public void updateMesh() {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), new int[]{2, 2});
            Main.WORLD.getFeaturesMap().get(this).forEach(feature -> {
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
