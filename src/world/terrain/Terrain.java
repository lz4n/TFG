package world.terrain;

import main.Main;
import utils.render.mesh.MaxSizedMesh;
import utils.render.texture.AnimatedTexture;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.worldBuilder.Biome;

public class Terrain {
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

    public enum TerrainType {
        WATER(new AnimatedTexture("assets/textures/terrain/water", 5, 8)),
        GRASS(new StaticTexture("assets/textures/terrain/grass.png")),
        SAND(new StaticTexture("assets/textures/terrain/sand.png")),
        STONE(new StaticTexture("assets/textures/terrain/stone.png")),
        SNOW(new StaticTexture("assets/textures/terrain/snow.png")),
        GRAVEL(new StaticTexture("assets/textures/terrain/gravel.png"));

        private final MaxSizedMesh MESH;

        TerrainType(Texture texture) {
            this.MESH = new MaxSizedMesh(Main.WORLD.getSize() * Main.WORLD.getSize(), texture);
        }

        public MaxSizedMesh getMesh() {
            return this.MESH;
        }
    }
}
