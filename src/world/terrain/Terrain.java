package world.terrain;

import main.Main;
import utils.render.mesh.WorldMesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.worldBuilder.Biome;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa una celda del terreno del mundo del juego. Contiene el tipo de terreno, el bioma y los parámetros de generación
 * que contiene la celda.
 */
public class Terrain implements Serializable {

    /**
     * Tipo de terreno que contiene la celda.
     * @see TerrainType
     */
    private final TerrainType TYPE;

    /**
     * Bioma de la celda.
     * @see Biome
     */
    private final Biome BIOME;

    /**
     * Parametros de la generación del mundo que tiene la celda.
     * @see world.worldBuilder.WorldBuilder
     */
    private final double CONTINENTALITY_NOISE, WEIRDNESS_NOISE, RIVERS_NOISE;

    /**
     * @param type Tipo de terreno.
     * @param biome Bioma de la casilla.
     * @param continentalityNoise Valor del mapa de ruido de continentalidad.
     * @param weirdnessNoise Valor del mapa de ruido de rareza.
     * @param riversNoise Valor del mapa de ruido de rios.
     */
    public Terrain(TerrainType type, Biome biome, double continentalityNoise, double weirdnessNoise, double riversNoise) {
        this.TYPE = type;
        this.BIOME = biome;
        this.CONTINENTALITY_NOISE = continentalityNoise;
        this.WEIRDNESS_NOISE = weirdnessNoise;
        this.RIVERS_NOISE = riversNoise;
    }

    /**
     * @return Tipo de terreno que tiene la celda.
     */
    public TerrainType getType() {
        return this.TYPE;
    }


    /**
     * @return Bioma de la celda.
     */
    public Biome getBiome() {
        return BIOME;
    }

    /**
     * @return Valor del mapa de continentalidad de la casilla.
     */
    public double getContinentalityNoise() {
        return CONTINENTALITY_NOISE;
    }

    /**
     * @return Valor del mapa de rareza de la casilla.
     */
    public double getWeirdnessNoise() {
        return WEIRDNESS_NOISE;
    }

    /**
     * @return Valor del mapa de rios de la casilla.
     */
    public double getRiversNoise() {
        return RIVERS_NOISE;
    }

    /**
     * Son los diferentes materiales que componen el terreno del mundo.
     */
    public enum TerrainType implements Serializable {
        WATER(Textures.WATER, false),
        GRASS(Textures.GRASS, true),
        SAND(Textures.SAND, true),
        STONE(Textures.STONE, true),
        SNOW(Textures.SNOW, true),
        GRAVEL(Textures.GRAVEL, true);

        /**
         * Mesh que se utiliza para el renderizado del terreno.
         * @see utils.render.mesh.Mesh
         */
        private final WorldMesh MESH;

        /**
         * Textura del tipo de terreno.
         */
        private final Texture TEXTURE;

        /**
         * @param texture Textura del tipo de terreno.
         * @param hasRandomUV Determina si se van a randomizar las coordenadas UV del terreno.
         */
        TerrainType(Texture texture, boolean hasRandomUV) {
            if (hasRandomUV) {
                this.MESH = new WorldMesh(Main.world.getSize() * Main.world.getSize(), new int[]{2, 2},
                        () -> switch (new Random().nextInt(4)) {
                            case 0 -> new int[]{1, 1, 0, 0, 1, 0, 0, 1};
                            case 1 -> new int[]{1, 1, 0, 0, 0, 1, 1, 0};
                            case 2 -> new int[]{0, 0, 1, 1, 1, 0, 0, 1};
                            default -> new int[]{0, 0, 1, 1, 0, 1, 1, 0};
                });
            } else {
                this.MESH = new WorldMesh(Main.world.getSize() * Main.world.getSize(), 2, 2);
            }
            this.TEXTURE = texture;
        }

        /**
         * @return Mesh que está utilizando el tipo de terreno.
         */
        public WorldMesh getMesh() {
            return this.MESH;
        }

        /**
         * @return Textura del tipo de terreno.
         */
        public Texture getTexture() {
            return this.TEXTURE;
        }
    }
}
