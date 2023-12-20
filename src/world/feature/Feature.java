package world.feature;

import main.Main;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

import java.util.*;
import java.util.function.Consumer;

public abstract class Feature implements Comparable<Feature> {
    protected static final Random RANDOM = new Random();
    private final Location LOCATION;
    private final Vector2i SIZE_IN_BLOCKS;
    private final FeatureType FEATURE_TYPE;
    private final int VARIANT;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
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
        this.VARIANT = variant;
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

    public boolean canBePlaced() {
        int posX = (int) this.getLocation().getX(), posY = (int) this.getLocation().getY();
        for (int x = 0; x < this.getSize().x(); x++) {
            for (int y = 0; y < this.getSize().y(); y++) {
                if (Main.WORLD.getFeature(posX +x, posY +y) != null) return false;
            }
        }
        return checkSpecificConditions();
    }

    protected abstract boolean checkSpecificConditions();

    public abstract Vector2i getRandomOffset();

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
                Textures.ROCK
        ),
        FLOWER(
                Textures.TULIP,
                Textures.TULIP_2,
                Textures.BLUE_ORCHID,
                Textures.DANDELION,
                Textures.RED_LILY
        ),
        BUSH(
                Textures.BUSH
        ),
        TREE(
                Textures.TREE1,
                Textures.TREE2
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

        public Feature createFeature(Location location, int variant) {
            switch (this) {
                case ROCK -> {
                    return new Rock(location);
                }
                case BUSH -> {
                    return new Bush(location);
                }
                case TREE -> {
                    return new Tree(location, variant);
                }
                case FLOWER -> {
                    return new Flower(location, variant);
                }
            }
            return null;
        }

        public Feature createFeature(Location location) {
            return createFeature(location, Main.RANDOM.nextInt(this.VARIANTS));
        }
    }
}
