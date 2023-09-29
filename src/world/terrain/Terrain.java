package world.terrain;

import main.Main;
import utils.render.mesh.MaxSizedMesh;
import utils.render.texture.AnimatedTexture;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;

public class Terrain {
    private final TerrainType TYPE;

    public Terrain(TerrainType type) {
        this.TYPE = type;
    }

    public TerrainType getType() {
        return this.TYPE;
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
