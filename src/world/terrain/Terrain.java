package world.terrain;

import main.Main;
import utils.render.mesh.WorldMesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.worldBuilder.Biome;

import java.io.Serializable;
import java.util.Random;

public class Terrain implements Serializable {
    private final TerrainType TYPE;
    private final Biome BIOME;
    private final double CONTINENTALITY_NOISE, WEIRDNESS_NOISE, RIVERS_NOISE;

    public Terrain(TerrainType type, Biome biome, double continentalityNoise, double weirdnessNoise, double riversNoise) {
        this.TYPE = type;
        this.BIOME = biome;
        this.CONTINENTALITY_NOISE = continentalityNoise;
        this.WEIRDNESS_NOISE = weirdnessNoise;
        this.RIVERS_NOISE = riversNoise;
    }

    public TerrainType getType() {
        return this.TYPE;
    }

    public Biome getBiome() {
        return BIOME;
    }

    public double getContinentalityNoise() {
        return CONTINENTALITY_NOISE;
    }

    public double getWeirdnessNoise() {
        return WEIRDNESS_NOISE;
    }

    public double getRiversNoise() {
        return RIVERS_NOISE;
    }

    public enum TerrainType implements Serializable {
        WATER(Textures.WATER, false),
        GRASS(Textures.GRASS, true),
        SAND(Textures.SAND, true),
        STONE(Textures.STONE, true),
        SNOW(Textures.SNOW, true),
        GRAVEL(Textures.GRAVEL, true);

        private final WorldMesh MESH;
        private final Texture TEXTURE;

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

        public WorldMesh getMesh() {
            return this.MESH;
        }

        public Texture getTexture() {
            return this.TEXTURE;
        }
    }
}
