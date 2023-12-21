package world;

import main.Main;
import utils.Logger;
import world.entity.Entity;
import world.feature.*;
import world.location.Location;
import world.particle.CloudParticle;
import world.particle.Particle;
import world.terrain.Terrain;
import world.tick.Ticking;
import world.worldBuilder.Biome;
import world.worldBuilder.WorldBuilder;

import java.util.*;

public class World extends Thread {
    private static final Random RANDOM = new Random();
    private static final int SUNRISE_DURATION = 6000,
            DAY_DURATION = 48000,
            NIGHT_DURATION = 30000,
            TOTAL_DAY_DURATION = World.SUNRISE_DURATION +World.DAY_DURATION +World.NIGHT_DURATION +World.SUNRISE_DURATION,
            SEASON_DURATION_IN_DAYS = 7,
            MAX_SEASON = 3;
    private static final float MIN_DAYLIGHT = .2f;

    private final int SEED, WORLD_SIZE;
    public final WorldBuilder BUILDER;
    private final Terrain[] TERRAIN;
    private final Feature[] FEATURES;
    private final Map<Feature.FeatureType, TreeSet<Feature>> FEATURES_MAP = new TreeMap<>();
    private final Map<Entity.EntityType, LinkedList<Entity>> ENTITITES_MAP = new HashMap<>();
    private final List<Particle> PARTICLES_LIST = new LinkedList<>();
    private int dayTime = (int) (World.SUNRISE_DURATION *.45), days, season, years, featuresCount, entitiesCount;
    private float happiness = 1;

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
                    feature = Feature.FeatureType.TREE.createFeature(new Location(x, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.6) feature = Feature.FeatureType.BUSH.createFeature(new Location(x, y + World.RANDOM.nextFloat() / 2));
            }
            case FOREST -> {
                if (World.RANDOM.nextFloat() >= 0.75)
                    feature = Feature.FeatureType.TREE.createFeature(new Location(x + World.RANDOM.nextFloat() / 4, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.84) feature = Feature.FeatureType.BUSH.createFeature(new Location(x, y + World.RANDOM.nextFloat() / 2));
            }
            case PLAINS -> {
                if (World.RANDOM.nextFloat() >= 0.97)
                    feature = Feature.FeatureType.TREE.createFeature(new Location(x + World.RANDOM.nextFloat() / 2, y + World.RANDOM.nextFloat() / 4));
                else if (World.RANDOM.nextFloat() >= 0.87) feature = Feature.FeatureType.BUSH.createFeature(new Location(x, y + World.RANDOM.nextFloat() / 2));
                else if (World.RANDOM.nextFloat() >= 0.75) feature = Feature.FeatureType.FLOWER.createFeature(new Location(x, y + World.RANDOM.nextFloat() / 4));
            }
            case MOUNTAIN -> {
                if (World.RANDOM.nextFloat() >= 0.98)
                    feature = Feature.FeatureType.ROCK.createFeature(new Location(x + World.RANDOM.nextFloat() / 4, y + World.RANDOM.nextFloat() / 6));
            }
        }
        if (feature != null && feature.canBePlaced()) {
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
        try {
            int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
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
        return null;
    }

    public Feature addFeature(Feature feature) {
        return this.addFeature(feature, true);
    }

    public void removeFeature(Feature feature) {
        try {
            int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
            for (int x = 0; x < feature.getSize().x(); x++) {
                for (int y = 0; y < feature.getSize().y(); y++) {
                    this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = null;
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

    public float getDayLight() {
        int totalTime = 0;
        int phaseDuration, faseActual;

        if (this.getDayTime() < World.SUNRISE_DURATION) {
            phaseDuration = World.SUNRISE_DURATION;
            faseActual = 0; // Amanecer
        } else if (this.getDayTime() < World.SUNRISE_DURATION  +World.DAY_DURATION) {
            faseActual = 1; // Día
            phaseDuration = World.DAY_DURATION;
            totalTime = World.SUNRISE_DURATION;
        } else if (this.getDayTime() < World.SUNRISE_DURATION +World.DAY_DURATION +World.SUNRISE_DURATION) {
            faseActual = 2; // Anochecer
            phaseDuration = World.SUNRISE_DURATION;
            totalTime = World.SUNRISE_DURATION +World.DAY_DURATION;
        } else {
            phaseDuration = World.NIGHT_DURATION;
            faseActual = 3; // Noche
            totalTime = World.SUNRISE_DURATION +World.DAY_DURATION +World.SUNRISE_DURATION;
        }

        // Calcular la intensidad de luz dentro de la fase actual
        float fraccionFaseActual = (float) (this.getDayTime() - totalTime) / phaseDuration;

        switch (faseActual) {
            case 0: // Amanecer
                return World.MIN_DAYLIGHT +(1 -World.MIN_DAYLIGHT) *fraccionFaseActual; // La intensidad aumenta durante el amanecer
            case 1: // Día
                return 1.0f; // Máxima intensidad durante el día
            case 2: // Anochecer
                return World.MIN_DAYLIGHT +World.MIN_DAYLIGHT *(1f - fraccionFaseActual); // La intensidad disminuye durante el anochecer
            case 3: // Noche
                return World.MIN_DAYLIGHT; // Sin luz durante la noche
            default:
                return 0.0f;
        }
    }

    public String getFormattedDayTime() {
        return String.format("%02d:%02d",
                this.getDayTime() *24 /World.TOTAL_DAY_DURATION,
                this.getDayTime() *24 %World.TOTAL_DAY_DURATION *60 /World.TOTAL_DAY_DURATION
                );
    }

    public int getDay() {
        return this.days;
    }

    public int getSeason() {
        return this.season;
    }

    public int getYear() {
        return this.years;
    }

    public float getHappiness() {
        return this.happiness;
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

        this.dayTime++;
        if (this.dayTime >= World.TOTAL_DAY_DURATION) {
            //TODO Mecánica de felicidad, que sea random cada dia es temporal.
            happiness = Main.RANDOM.nextFloat();
            Logger.sendMessage("DÍA", Logger.LogMessageType.DEBUG);
            this.dayTime = 0;

            this.days++;
            if (this.days >= World.SEASON_DURATION_IN_DAYS) {
                this.season++;
                this.days = 0;

                if (this.season > World.MAX_SEASON) {
                    this.years++;
                    this.season = 0;
                }
            }
        }

        if (Main.RANDOM.nextFloat() > ((this.season >= 1 && this.season <= 2)?0.965: 0.98)) {
            float cloudVelocity = Main.RANDOM.nextFloat(.04f, .06f);
            Location cloudLocation = new Location(0, Main.RANDOM.nextFloat(this.WORLD_SIZE));
            this.spawnParticle(new CloudParticle(cloudLocation.clone(), cloudVelocity, false));
            cloudLocation.add(0, Main.RANDOM.nextFloat(2f, 5f) *-1);
            this.spawnParticle(new CloudParticle(cloudLocation, cloudVelocity, true));
        }

        Ticking.getTickableObjets().forEach(tickable -> tickable.onTick(deltaTime));
    }
}
