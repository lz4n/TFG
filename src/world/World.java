package world;

import org.joml.Vector2d;
import world.terrain.Terrain;
import world.worldBuilder.Biome;
import world.worldBuilder.FastNoiseLite;
import world.worldBuilder.WorldBuilder;

import java.util.*;

public class World {
    private static final Random RANDOM = new Random();


    private final int SEED, WORLD_SIZE;
    private final WorldBuilder BUILDER;
    private final Terrain[] TERRAIN;
    private final Biome[] BIOME;

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

    private int mapCoordinatesToIndex(int x, int y) {
        return x * this.WORLD_SIZE + y;
    }
}
