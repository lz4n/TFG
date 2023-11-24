package world.feature;

import main.Main;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

import java.util.*;

public abstract class Feature implements Comparable<Feature> {
    protected static final Random RANDOM = new Random();
    private final Location LOCATION;
    private final Vector2i SIZE_IN_BLOCKS;
    private final FeatureType FEATURE_TYPE;
    private final int VARIANT;

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
        this.VARIANT = Feature.RANDOM.nextInt(featureType.VARIANTS);
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

    public int getVariant() {
        return this.VARIANT;
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
        ROCK(
                new StaticTexture("assets/textures/feature/rock/rock.png")
        ),
        FLOWER(
                new StaticTexture("assets/textures/feature/flower/tulip.png"),
                new StaticTexture("assets/textures/feature/flower/tulip2.png"),
                new StaticTexture("assets/textures/feature/flower/blue_orchid.png"),
                new StaticTexture("assets/textures/feature/flower/dandelion.png"),
                new StaticTexture("assets/textures/feature/flower/red_lily.png")
        ),
        BUSH(
                new StaticTexture("assets/textures/feature/bush.png")
        ),
        TREE(
                new StaticTexture("assets/textures/feature/tree.png"),
                new StaticTexture("assets/textures/feature/tree2.png")
        );

        private WorldMesh mesh;
        private final List<Texture> TEXTURES;
        private final int VARIANTS;

        FeatureType(Texture... textures) {
            this.mesh = new WorldMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), 2, 2, 1);
            this.TEXTURES = Arrays.asList(textures);
            this.VARIANTS = textures.length;
        }

        public void updateMesh() {
            Set<Feature> features = Main.WORLD.getFeaturesMap().get(this);
            this.mesh = new WorldMesh(features.size(), 2, 2, 1);
            features.forEach(feature -> mesh.addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getSize().x(), feature.getSize().y(), feature.VARIANT));
            this.mesh.load();
        }

        public WorldMesh getMesh() {
            return this.mesh;
        }

        public List<Texture> getTextures() {
            return new LinkedList<>(this.TEXTURES);
        }

        public int getVariants() {
            return this.VARIANTS;
        }
    }
}
