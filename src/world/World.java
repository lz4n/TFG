package world;

import org.joml.Vector2d;
import world.feature.Bush;
import world.feature.Feature;
import world.feature.Tree;
import world.location.Location;
import world.terrain.Terrain;
import world.worldBuilder.Biome;
import world.worldBuilder.WorldBuilder;

import java.util.*;

public class World extends Thread {
    private static final Random RANDOM = new Random();
    private static final int DAY_DURATION = 3600, HALF_DAY_DURATION = World.DAY_DURATION / 2;


    private final int SEED, WORLD_SIZE;
    private final WorldBuilder BUILDER;
    private final Terrain[] TERRAIN;
    private final Biome[] BIOME;
    private final Feature[] FEATURE;
    private int dayTime;

    public World(int seed, int worldSize) {
        this.SEED = seed;
        this.WORLD_SIZE = worldSize;
        this.TERRAIN = new Terrain[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BIOME = new Biome[this.WORLD_SIZE * this.WORLD_SIZE];
        this.FEATURE = new Feature[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BUILDER = new WorldBuilder(this.SEED);
    }

    private Terrain generateTerrain(int x, int y) {
        double continentality, weirdness, rivers;
        Biome biome;
        continentality = this.BUILDER.getContinentalityAt(x, y);
        weirdness = this.BUILDER.getWeirdnessAt(x, y);
        rivers = this.BUILDER.getRiversAt(x, y);

        biome = Biome.generateBiome(continentality, weirdness, rivers);

        switch (biome) {
            case FOREST:
                if (World.RANDOM.nextFloat() >= 0.75) new Tree(new Location(x + World.RANDOM.nextDouble() /4, y + World.RANDOM.nextDouble() /4));
                else if (World.RANDOM.nextFloat() >= 0.8) new Bush(new Location(x, y + World.RANDOM.nextDouble() /2));
                break;
            case PLAINS:
                if (World.RANDOM.nextFloat() >= 0.97) new Tree(new Location(x + World.RANDOM.nextDouble() /2, y + World.RANDOM.nextDouble() /4));
                else if (World.RANDOM.nextFloat() >= 0.6) new Bush(new Location(x, y + World.RANDOM.nextDouble() /2));
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

    public Biome getBiome(int x, int y) {
        return BIOME[this.mapCoordinatesToIndex(x, y)];
    }

    public void setBiome(int x, int y, Biome biome) {
        BIOME[this.mapCoordinatesToIndex(x, y)] = biome;
    }

    public Feature getFeature(int x, int y) {
        try {
            return FEATURE[this.mapCoordinatesToIndex(x, y)];
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

    private int mapCoordinatesToIndex(int x, int y) {
        return x * this.WORLD_SIZE + y;
    }

    public int getDayTime() {
        return this.dayTime;
    }

    public double getDayLight() {
        if (this.getDayTime() < World.HALF_DAY_DURATION) {
            return 0.4 + 0.6 * ((double) this.getDayTime() / World.HALF_DAY_DURATION);
        } else {
            return 1.0 - 0.6 * ((double) (this.getDayTime() - World.HALF_DAY_DURATION) / World.HALF_DAY_DURATION);
        }
    }

    public void onTick() {
        dayTime++;
        if (dayTime > World.DAY_DURATION) {
            System.err.println("DIA");
            dayTime = 0;
        }
    }
}
