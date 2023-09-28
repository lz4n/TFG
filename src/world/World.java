package world;

import org.joml.Vector2d;
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
    private final List<Feature> FEATURES = new LinkedList<>();

    public World(int seed, int worldSize) {
        this.SEED = seed;
        this.WORLD_SIZE = worldSize;
        this.TERRAIN = new Terrain[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BIOME = new Biome[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BUILDER = new WorldBuilder(this.SEED);
    }

    private Terrain generateTerrain(int x, int y) {
        double continentality;
        Biome biome;
        continentality = this.BUILDER.getContinentalityAt(x, y);
        biome = Biome.generateBiome(continentality);

        switch (biome) {
            case FOREST:
                if (World.RANDOM.nextFloat() >= 0.7) FEATURES.add(new Tree(new Vector2d(x + World.RANDOM.nextDouble(), y + World.RANDOM.nextDouble())));
                break;
            case PLAINS:
                if (World.RANDOM.nextFloat() >= 0.99) FEATURES.add(new Tree(new Vector2d(x + World.RANDOM.nextDouble(), y + World.RANDOM.nextDouble())));
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
