package world;

import org.joml.Vector2d;
import world.feature.Bush;
import world.feature.Feature;
import world.feature.Tree;
import world.terrain.Terrain;
import world.worldBuilder.Biome;
import world.worldBuilder.WorldBuilder;

import java.util.*;

public class World {
    private static final Random RANDOM = new Random();


    private final int SEED, WORLD_SIZE;
    private final WorldBuilder BUILDER;
    private final Terrain[] TERRAIN;
    private final Biome[] BIOME;
    private final Feature[] FEATURE;
    private final List<Feature> FEATURES = new LinkedList<>();

    public World(int seed, int worldSize) {
        this.SEED = seed;
        this.WORLD_SIZE = worldSize;
        this.TERRAIN = new Terrain[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BIOME = new Biome[this.WORLD_SIZE * this.WORLD_SIZE];
        this.FEATURE = new Feature[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BUILDER = new WorldBuilder(this.SEED);
    }

    private Terrain generateTerrain(int x, int y) {
        double continentality, weirdness;
        Biome biome;
        continentality = this.BUILDER.getContinentalityAt(x, y);
        weirdness = this.BUILDER.getWeirdnessAt(x, y);

        System.err.println(continentality + "<->" + weirdness);

        biome = Biome.generateBiome(continentality, weirdness);

        switch (biome) {
            case FOREST:
                if (World.RANDOM.nextFloat() >= 0.75) FEATURES.add(new Tree(new Vector2d(x + World.RANDOM.nextDouble() /4, y + World.RANDOM.nextDouble() /4)));
                else if (World.RANDOM.nextFloat() >= 0.8) FEATURES.add(new Bush(new Vector2d(x, y + World.RANDOM.nextDouble() /2)));
                break;
            case PLAINS:
                if (World.RANDOM.nextFloat() >= 0.97) FEATURES.add(new Tree(new Vector2d(x + World.RANDOM.nextDouble() /2, y + World.RANDOM.nextDouble() /4)));
                else if (World.RANDOM.nextFloat() >= 0.6) FEATURES.add(new Bush(new Vector2d(x, y + World.RANDOM.nextDouble() /2)));
                break;
        }

        this.setBiome(x, y, biome);
        this.setTerrain(x, y, biome.getTerrain());

        return biome.getTerrain();
    }

    public Terrain getTerrain(int x, int y) {
        Terrain terrain = this.TERRAIN[this.mapCoordinatesToIndex(x, y)];

        if (terrain == null) {
            terrain = this.generateTerrain(x, y);
        }
        return terrain;
    }

    public void setTerrain(int x, int y, Terrain terrain) {
        TERRAIN[this.mapCoordinatesToIndex(x, y)] = terrain;
    }

    public Biome getBiome(Vector2d location) {
        return BIOME[this.mapCoordinatesToIndex((int) location.x(), (int) location.y())];
    }

    public void setBiome(int x, int y, Biome biome) {
        BIOME[this.mapCoordinatesToIndex(x, y)] = biome;
    }

    public Feature getFeature(Vector2d location) {
        try {
            return FEATURE[this.mapCoordinatesToIndex((int) location.x(), (int) location.y())];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public void setFeature(int x, int y, Feature feature) {
        try {
            FEATURE[this.mapCoordinatesToIndex(x, y)] = feature;
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }

    public int getSize() {
        return this.WORLD_SIZE;
    }

    public List<Feature> getFeatures() {
        return new LinkedList<>(this.FEATURES);
    }

    private int mapCoordinatesToIndex(int x, int y) {
        return x * this.WORLD_SIZE + y;
    }
}
