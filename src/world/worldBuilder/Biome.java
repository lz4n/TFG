package world.worldBuilder;

import world.terrain.Terrain;

public enum Biome {
    OCEAN(new double[]{0, -2}, Terrain.TerrainType.WATER),
    BEACH(new double[]{0.3, 0}, Terrain.TerrainType.SAND),
    PLAINS(new double[]{0.55, 0.3}, Terrain.TerrainType.GRASS),
    FORES(new double[]{0.7, 0.55}, Terrain.TerrainType.GRASS),
    MOUNTAIN(new double[]{1.1, 0.7}, Terrain.TerrainType.STONE),
    SNOWY_MOUNTAIN(new double[]{2, 1.1}, Terrain.TerrainType.SNOW);

    private final double[] NOISE;
    private final Terrain.TerrainType TERRAIN_BASE_TYPE;

    Biome(double[] noise,  Terrain.TerrainType terrainBaseType) {
        this.NOISE = noise;
        this.TERRAIN_BASE_TYPE = terrainBaseType;
    }

    public Terrain getTerrain() {
        return new Terrain(this.TERRAIN_BASE_TYPE);
    }

    public static Biome generateBiome(double continentality) {
        for (Biome biome: Biome.values()) {
            if (continentality <= biome.NOISE[0] && continentality > biome.NOISE[1]) return biome;
        }
        return Biome.OCEAN;
    }
}
