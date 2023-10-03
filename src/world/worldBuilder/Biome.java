package world.worldBuilder;

import org.joml.Vector3f;
import world.terrain.Terrain;

import javax.swing.*;

public enum Biome {
    OCEAN(new double[]{-0.3, -2,                    2, -2,              2, -2}, Terrain.TerrainType.WATER),
    RIVER(new double[]{0.9, -0.3,                   2, -2,              0.07, -0.07}, Terrain.TerrainType.WATER),
    RIVER_DELTA(new double[]{-0.2, -0.35,           2, -2,              0.1, -0.1}, Terrain.TerrainType.WATER),
    RIVER_SHORE(new double[]{0.9, -0.3,             2, -2,              0.15, -0.15}, Terrain.TerrainType.GRAVEL),
    RIVER_MOUNTAIN_SHORE(new double[]{0.9, 0.65,    2, -2,              0.28, -0.28}, Terrain.TerrainType.GRASS, new Vector3f(18, 105, 5)),
    BEACH(new double[]{-0.25, -0.3,                 0, -2,              2, -2}, Terrain.TerrainType.SAND),
    GRAVEL_BEACH(new double[]{-0.25, -0.3,          2, 0,               2, -2}, Terrain.TerrainType.GRAVEL),
    PLAINS(new double[]{0.55, -0.25,                0, -2,              2, -2}, Terrain.TerrainType.GRASS, new Vector3f(28, 161, 8)),
    FOREST(new double[]{0.7, -0.25,                 2, -2,              2, -2}, Terrain.TerrainType.GRASS, new Vector3f(18, 105, 5)),
    MOUNTAIN(new double[]{1.1, 0.7,                 2, -2,              2, -2}, Terrain.TerrainType.STONE),
    SNOWY_MOUNTAIN(new double[]{2, 1.1,             2, -2,              2, -2}, Terrain.TerrainType.SNOW);

    public final double[] NOISE;
    private final Terrain.TerrainType TERRAIN_BASE_TYPE;
    private final Vector3f GRASS_COLOR;

    Biome(double[] noise, Terrain.TerrainType terrainBaseType, Vector3f grassColor) {
        this.NOISE = noise;
        this.TERRAIN_BASE_TYPE = terrainBaseType;
        this.GRASS_COLOR = grassColor;
    }

    Biome(double[] noise, Terrain.TerrainType terrainBaseType) {
        this(noise, terrainBaseType, new Vector3f(1, 1, 1));
    }

    public Terrain.TerrainType getTerrainType() {
        return this.TERRAIN_BASE_TYPE;
    }

    public Vector3f getGrassColor() {
        return this.GRASS_COLOR;
    }

    public static Biome generateBiome(double continentality, double weirdness, double river) {
        for (Biome biome: Biome.values()) {
            if (continentality <= biome.NOISE[0] && continentality > biome.NOISE[1] &&
            weirdness <= biome.NOISE[2] && weirdness > biome.NOISE[3] &&
            river <= biome.NOISE[4] && river > biome.NOISE[5]) return biome;
        }
        return Biome.OCEAN;
    }
}
