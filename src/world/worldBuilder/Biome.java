package world.worldBuilder;

import org.joml.Vector3f;
import world.terrain.Terrain;

import javax.swing.*;

/**
 * Diferentes biomas que tiene el juego. Los biomas determinan algunos comportamientos y el terreno de esa celda.
 */
public enum Biome {
    OCEAN(new double[]{-0.3, -2,                    2, -2,              2, -2}, Terrain.TerrainType.WATER),
    RIVER(new double[]{0.9, -0.3,                   2, -2,              0.07, -0.07}, Terrain.TerrainType.WATER),
    RIVER_DELTA(new double[]{-0.2, -0.35,           2, -2,              0.1, -0.1}, Terrain.TerrainType.WATER),
    RIVER_SHORE(new double[]{0.9, -0.3,             2, -2,              0.15, -0.15}, Terrain.TerrainType.GRAVEL),
    RIVER_MOUNTAIN_SHORE(new double[]{0.9, 0.65,    2, -2,              0.28, -0.28}, Terrain.TerrainType.GRASS),
    BEACH(new double[]{-0.25, -0.3,                 0, -2,              2, -2}, Terrain.TerrainType.SAND),
    GRAVEL_BEACH(new double[]{-0.25, -0.3,          2, 0,               2, -2}, Terrain.TerrainType.GRAVEL),
    PLAINS(new double[]{0.55, -0.25,                0, -2,              2, -2}, Terrain.TerrainType.GRASS),
    FOREST(new double[]{0.7, -0.25,                 2, -2,              2, -2}, Terrain.TerrainType.GRASS),
    MOUNTAIN(new double[]{1.1, 0.7,                 2, -2,              2, -2}, Terrain.TerrainType.STONE),
    SNOWY_MOUNTAIN(new double[]{2, 1.1,             2, -2,              2, -2}, Terrain.TerrainType.SNOW);

    /**
     * Parámetros de ruido máximos y mínimos para cada mapa de ruido.
     */
    public final double[] NOISE;

    /**
     * Tipo de terreno que va a generar el bioma.
     */
    private final Terrain.TerrainType TERRAIN_BASE_TYPE;

    /**
     * @param noise Parámetros de ruido máximos y mínimos para cada mapa de ruido.
     * @param terrainBaseType Tipo de terreno que va a generar el bioma.
     */
    Biome(double[] noise, Terrain.TerrainType terrainBaseType) {
        this.NOISE = noise;
        this.TERRAIN_BASE_TYPE = terrainBaseType;
    }

    /**
     * @return Tipo de terreno que va a generar el bioma.
     */
    public Terrain.TerrainType getTerrainType() {
        return this.TERRAIN_BASE_TYPE;
    }

    /**
     * Devuelve el bioma correspondiente a los valores de ruido que se pasan como parámetros.
     * @param continentality Parametro de continentalidad.
     * @param weirdness Parametro de rareza.
     * @param river Parametro de rios.
     * @return Bioma correspondiente a esos parámetros, <code>Biome.OCEAN</code> por defecto.
     * @see WorldBuilder
     * @see Biome#OCEAN
     */
    public static Biome generateBiome(double continentality, double weirdness, double river) {
        for (Biome biome: Biome.values()) {
            if (continentality <= biome.NOISE[0] && continentality > biome.NOISE[1] &&
            weirdness <= biome.NOISE[2] && weirdness > biome.NOISE[3] &&
            river <= biome.NOISE[4] && river > biome.NOISE[5]) return biome;
        }
        return Biome.OCEAN;
    }
}
