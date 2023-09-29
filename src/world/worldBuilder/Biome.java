package world.worldBuilder;

import main.Main;
import world.terrain.Terrain;

public enum Biome {
    OCEAN(new double[]{0, -2,               2, -2,      2, -2}, Terrain.TerrainType.WATER),
    //RIVER(new double[]{0.7, 0.05,           0.05, 0.001,    2, -2}, Terrain.TerrainType.WATER),
    BEACH(new double[]{0.05, 0,             0, -2,      2, -2}, Terrain.TerrainType.SAND),
    GRAVEL_BEACH(new double[]{0.05, 0,      2, 0,       2, -2}, Terrain.TerrainType.GRAVEL),
    PLAINS(new double[]{0.55, 0.05,         0, -2,      2, -2}, Terrain.TerrainType.GRASS),
    FOREST(new double[]{0.7, 0.05,          2, -2,      2, -2}, Terrain.TerrainType.GRASS),
    MOUNTAIN(new double[]{1.1, 0.7,         2, -2,      2, -2}, Terrain.TerrainType.STONE),
    SNOWY_MOUNTAIN(new double[]{2, 1.1,     2, -2,      2, -2}, Terrain.TerrainType.SNOW);

    private final double[] NOISE;
    private final Terrain.TerrainType TERRAIN_BASE_TYPE;

    Biome(double[] noise,  Terrain.TerrainType terrainBaseType) {
        this.NOISE = noise;
        this.TERRAIN_BASE_TYPE = terrainBaseType;
    }

    public Terrain getTerrain() {
        return new Terrain(this.TERRAIN_BASE_TYPE);
    }

    public static Biome generateBiome(double continentality, double weirdness, double river) {
        for (Biome biome: Biome.values()) {
            if (continentality <= biome.NOISE[0] && continentality > biome.NOISE[1] &&
            weirdness <= biome.NOISE[2] && continentality > biome.NOISE[3]) return biome;
        }
        return Biome.OCEAN;
    }
}
