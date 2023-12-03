package world;

import main.Main;
import org.joml.Vector2f;
import utils.Logger;
import world.entity.Entity;
import world.feature.*;
import world.location.Location;
import world.particle.Particle;
import world.terrain.Terrain;
import world.tick.Ticking;
import world.worldBuilder.Biome;
import world.worldBuilder.WorldBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class World extends Thread {
    private static final Random RANDOM = new Random();
    private static final int DAY_DURATION = 3600, HALF_DAY_DURATION = World.DAY_DURATION / 2;


    private final int SEED, WORLD_SIZE;
    public final WorldBuilder BUILDER;
    private final Terrain[] TERRAIN;
    private final Feature[] FEATURES;
    private final Map<Feature.FeatureType, TreeSet<Feature>> FEATURES_MAP = new TreeMap<>();
    private final Map<Entity.EntityType, LinkedList<Entity>> ENTITITES_MAP = new HashMap<>();
    private final List<Particle> PARTICLES_LIST = new LinkedList<>();
    private int dayTime, featuresCount, entitiesCount;

    public World(int seed, int worldSize) {
        this.SEED = seed;
        this.WORLD_SIZE = worldSize;
        this.TERRAIN = new Terrain[this.WORLD_SIZE * this.WORLD_SIZE];
        this.FEATURES = new Feature[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BUILDER = new WorldBuilder(this.SEED);
    }

    private Terrain generateTerrain(int x, int y) {
        double continentality, weirdness, rivers;
        Biome biome;
        Terrain terrain;
        Feature feature = null;

        continentality = this.BUILDER.getContinentalityAt(x, y);
        weirdness = this.BUILDER.getWeirdnessAt(x, y);
        rivers = this.BUILDER.getRiversAt(x, y);

        biome = Biome.generateBiome(continentality, weirdness, rivers);

        terrain = new Terrain(biome.getTerrainType(), biome, continentality, weirdness, rivers);
        this.setTerrain(x, y, terrain);
        switch (biome) {
            case RIVER_MOUNTAIN_SHORE -> {
                if (World.RANDOM.nextFloat() >= 0.4)
                    feature = new Tree(new Location(x, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.6) feature = new Bush(new Location(x, y + World.RANDOM.nextFloat() / 2));
            }
            case FOREST -> {
                if (World.RANDOM.nextFloat() >= 0.75)
                    feature = new Tree(new Location(x + World.RANDOM.nextFloat() / 4, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.84) feature = new Bush(new Location(x, y + World.RANDOM.nextFloat() / 2));
            }
            case PLAINS -> {
                if (World.RANDOM.nextFloat() >= 0.97)
                    feature = new Tree(new Location(x + World.RANDOM.nextFloat() / 2, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.87) feature = new Bush(new Location(x, y + World.RANDOM.nextFloat() / 2));
                else if (World.RANDOM.nextFloat() >= 0.75) feature = new Flower(new Location(x, y + World.RANDOM.nextFloat() / 4));
            }
            case MOUNTAIN -> {
                if (World.RANDOM.nextFloat() >= 0.98)
                    feature = new Rock(new Location(x + World.RANDOM.nextFloat() / 4, y + World.RANDOM.nextFloat() / 6));
            }
        }
        if (feature != null) {
            Main.WORLD.addFeature(feature, false);
        }
        return terrain;
    }

    public Terrain getTerrain(int x, int y) {
        Terrain terrain = this.TERRAIN[this.mapCoordinatesToIndex(x, y)];

        if (terrain == null) {
            terrain = this.generateTerrain(x, y);
        }
        return terrain;
    }

    public void setTerrain(int x, int y, Terrain terrain) {
        this.TERRAIN[this.mapCoordinatesToIndex(x, y)] = terrain;
    }


    public Feature getFeature(int x, int y) {
        try {
            return this.FEATURES[this.mapCoordinatesToIndex(x, y)];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public Feature addFeature(Feature feature, boolean updateMesh) {
        int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
        if (this.canFeatureOverlapsWithOtherFeature(feature)) {
            try {
                for (int x = 0; x < feature.getSize().x(); x++) {
                    for (int y = 0; y < feature.getSize().y(); y++) {
                        this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = feature;
                    }
                }
                Feature.FeatureType featureType = feature.getFeatureType();
                TreeSet<Feature> featureSet = this.FEATURES_MAP.getOrDefault(featureType, new TreeSet<>());
                featureSet.add(feature);
                this.FEATURES_MAP.put(featureType, featureSet);

                if (updateMesh) {
                    featureType.updateMesh();
                } else {
                    featureType.getMesh().addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getSize().x(), feature.getSize().y(), feature.getVariant());
                }

                this.featuresCount++;
                return feature;
            } catch (ArrayIndexOutOfBoundsException ignore) {
            }
        }
        return null;
    }

    public Feature addFeature(Feature feature) {
        return this.addFeature(feature, true);
    }

    public void removeFeature(Feature feature) {
        int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();

        try {
            for (int x = -1; x <= feature.getSize().x(); x++) {
                for (int y = -1; y <= feature.getSize().y(); y++) {
                    this.removeOverlapping(posX +x, posY +y, feature);

                }
            }
            Feature.FeatureType featureType = feature.getFeatureType();
            TreeSet<Feature> featureSet = this.FEATURES_MAP.getOrDefault(featureType, new TreeSet<>());
            featureSet.remove(feature);
            this.FEATURES_MAP.put(featureType, featureSet);

            featureType.updateMesh();

            this.featuresCount--;
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }
    }

    private void removeOverlapping(int posX, int posY, Feature origninalFeature) {
        Feature overlapsFeature = this.getFeature(posX, posY);
        if (overlapsFeature != null) {
            if (overlapsFeature == origninalFeature) {
                this.FEATURES[this.mapCoordinatesToIndex(posX, posY)] = null;
            } else {
                for (int overlapsX = posX; overlapsX < posX +overlapsFeature.getSize().x(); overlapsX++) {
                    for (int overlapsY = posY; overlapsY < posY +overlapsFeature.getSize().y(); overlapsY++) {
                        if (this.FEATURES[this.mapCoordinatesToIndex(overlapsX, overlapsY)] == null) {
                            this.FEATURES[this.mapCoordinatesToIndex(overlapsX, overlapsY)] = overlapsFeature;
                        }
                    }
                }
            }
        }
    }

    public boolean canFeatureOverlapsWithOtherFeature(Feature feature) {
        Feature feature1;
        for (int x = 0; x < feature.getSize().x(); x++) for (int y = 0; y < feature.getSize().y(); y++) {
            feature1 = feature.getLocation().add(x, y).getFeature();
            if (feature1 != null) {
                return false;
            }
        }
        return true;
    }

    public void spawnEntity(Entity entity) {
        Entity.EntityType entityType = entity.getEntityType();
        LinkedList<Entity> entitiesList = this.ENTITITES_MAP.getOrDefault(entityType, new LinkedList<>());
        entitiesList.add(entity);
        this.ENTITITES_MAP.put(entityType, entitiesList);

        this.entitiesCount++;
    }

    public void spawnParticle(Particle particle) {
        this.PARTICLES_LIST.add(particle);
    }

    public void removeParticle(Particle particle) {
        this.PARTICLES_LIST.remove(particle);
        particle.removeTicking();
    }

    public int getSeed() {
        return this.SEED;
    }

    public int getSize() {
        return this.WORLD_SIZE;
    }

    private int mapCoordinatesToIndex(int x, int y) {
        return x * this.WORLD_SIZE + y;
    }

    public int getDayTime() {
        return this.dayTime;
    }

    public int getFeaturesCount() {
        return this.featuresCount;
    }

    public int getEntitiesCount() {
        return this.entitiesCount;
    }

    public double getDayLight() {
        if (this.getDayTime() < World.HALF_DAY_DURATION) {
            return 0.4 + 0.6 * ((double) this.getDayTime() / World.HALF_DAY_DURATION);
        } else {
            return 1.0 - 0.6 * ((double) (this.getDayTime() - World.HALF_DAY_DURATION) / World.HALF_DAY_DURATION);
        }
    }

    public Map<Feature.FeatureType, TreeSet<Feature>> getFeaturesMap() {
        return new HashMap<>(this.FEATURES_MAP);
    }

    public Map<Entity.EntityType, LinkedList<Entity>> getEntitiesMap() {
        return new HashMap<>(this.ENTITITES_MAP);
    }

    public List<Particle> getParticlesList() {
        return new LinkedList<>(this.PARTICLES_LIST);
    }

    public void tick(long deltaTime) {
        dayTime++;
        if (dayTime > World.DAY_DURATION) {
            Logger.sendMessage("DÍA", Logger.LogMessageType.DEBUG);
            dayTime = 0;
        }

        Ticking.getTickableObjets().forEach(tickable -> tickable.onTick(deltaTime));
    }
}
