package world.terrain;

public class Terrain {
    private final TerrainType TYPE;

    public Terrain(TerrainType type) {
        this.TYPE = type;
    }

    public TerrainType getType() {
        return this.TYPE;
    }

    public enum TerrainType {
        WATER("assets/textures/terrain/water.png"),
        GRASS("assets/textures/terrain/grass.png"),
        SAND("assets/textures/terrain/sand.png"),
        STONE("assets/textures/terrain/stone.png"),
        SNOW("assets/textures/terrain/snow.png"),
        GRAVEL("assets/textures/terrain/gravel.png");

        private final String TEXTURE;

        TerrainType(String texture) {
            TEXTURE = texture;
        }

        public String getTexture() {
            return this.TEXTURE;
        }
    }
}
