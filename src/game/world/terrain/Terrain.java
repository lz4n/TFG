package game.world.terrain;

import game.Main;
import game.utils.Logger;
import game.utils.render.mesh.Mesh;
import game.utils.render.mesh.WorldMesh;
import game.utils.render.scene.WorldScene;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.world.worldBuilder.Biome;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa una celda del terreno del mundo del juego. Contiene el tipo de terreno, el bioma y los parámetros de generación
 * que contiene la celda.
 */
public class Terrain implements Serializable {
    private final TerrainType TYPE;
    private final Biome BIOME;
    private final double CONTINENTALITY_NOISE, WEIRDNESS_NOISE, RIVERS_NOISE;

    /**
     * Constructor para la clase Terrain.
     *
     * @param type Tipo de terreno.
     * @param biome Bioma de la casilla.
     * @param continentalityNoise Valor del mapa de ruido de continentalidad.
     * @param weirdnessNoise Valor del mapa de ruido de rareza.
     * @param riversNoise Valor del mapa de ruido de ríos.
     */
    public Terrain(TerrainType type, Biome biome, double continentalityNoise, double weirdnessNoise, double riversNoise) {
        this.TYPE = type;
        this.BIOME = biome;
        this.CONTINENTALITY_NOISE = continentalityNoise;
        this.WEIRDNESS_NOISE = weirdnessNoise;
        this.RIVERS_NOISE = riversNoise;
    }

    /**
     * Añade el terreno al mesh en la posición dada.
     *
     * @param x Coordenada x de la celda.
     * @param y Coordenada y de la celda.
     */
    public void addToMesh(int x, int y) {
        this.getType().getMesh().addVertex(
                x -(this.getType().HAS_CONNECTED_TEXTURES ? (1f/ WorldScene.SPRITE_SIZE) : 0),
                y -(this.getType().HAS_CONNECTED_TEXTURES ? (1f/WorldScene.SPRITE_SIZE) : 0),
                1 +(this.getType().HAS_CONNECTED_TEXTURES ? (1f/WorldScene.SPRITE_SIZE) *2 : 0),
                1 +(this.getType().HAS_CONNECTED_TEXTURES ? (1f/WorldScene.SPRITE_SIZE) *2 : 0)
        );
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

    public int getPathCost() {
        return this.getType().PATH_COST;
    }

    /**
     * Son los diferentes materiales que componen el terreno del mundo.
     */
    public enum TerrainType implements Serializable {
        WATER(Textures.WATER, false, 1, false, false),
        STONE_TILE(Textures.STONE_TILE, true, -1, false, false),
        PATH(Textures.PATH, true, -1,true, true),
        SAND(Textures.SAND, true, 1,false, true),
        GRAVEL(Textures.GRAVEL, true, 2,true, true),
        GRASS(Textures.GRASS, true, 2,false, true),
        STONE(Textures.STONE, true, 3,true, true),
        SNOW(Textures.SNOW, false, 5,true, true);

        private final boolean IS_BUILDABLE;
        private final int PATH_COST;
        private final boolean HAS_RANDOM_UV;
        private final boolean HAS_CONNECTED_TEXTURES;

        /**
         * Mesh que se utiliza para el renderizado del terreno.
         * @see Mesh
         */
        private WorldMesh mesh;

        /**
         * Textura del tipo de terreno.
         */
        private final Texture TEXTURE;

        /**
         * Constructor para la enumeración TerrainType.
         *
         * @param texture Textura del tipo de terreno.
         * @param isBuildable Determina si se puede construir en este terreno.
         * @param pathCost Costo de camino del terreno.
         * @param hasRandomUV Determina si se van a randomizar las coordenadas UV del terreno.
         * @param hasConnectedTextures Determina si las texturas se van a conectar con la textura de otros terrenos.
         */
        TerrainType(Texture texture, boolean isBuildable, int pathCost, boolean hasRandomUV, boolean hasConnectedTextures) {
            this.IS_BUILDABLE = isBuildable;
            this.PATH_COST = pathCost;
            this.HAS_RANDOM_UV = hasRandomUV;
            this.HAS_CONNECTED_TEXTURES = hasConnectedTextures;
            this.mesh = this.createMesh();
            this.TEXTURE = texture;
        }

        private WorldMesh createMesh() {
            if (this.HAS_RANDOM_UV) {
                return new WorldMesh(Main.world.getSize() * Main.world.getSize(), new int[]{2, 2},
                        (posX, posY) -> switch (new Random((long) Main.world.getSeed() *Main.world.mapCoordinatesToIndex(posX, posY)).nextInt(4)) {
                            case 0 -> new int[]{0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1};
                            case 1 -> new int[]{1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1};
                            case 2 -> new int[]{0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0};
                            default -> new int[]{1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0};
                        });
            }
            return new WorldMesh(Main.world.getSize() * Main.world.getSize(), 2, 2);
        }

        /**
         * Actualiza el mesh del terreno.
         */
        public void updateMesh() {
            this.mesh = this.createMesh();
            Thread thread = new Thread(() -> {
                for (int x = 0; x < Main.world.getSize(); x++) for (int y = Main.world.getSize() -1; y >=0; y--) {
                    if (Main.world.getTerrain(x, y).getType().equals(this)) {
                        Main.world.getTerrain(x, y).addToMesh(x, y);
                    }
                }
            });

            thread.start();
            try {
                thread.join();
                this.mesh.adjust();
                this.mesh.load();
            } catch (InterruptedException exception) {
                Logger.sendMessage("Error encolando hilo de recalculado de terreno: %s.", Logger.LogMessageType.FATAL, exception.getMessage());
            }
        }

        /**
         * Reestablece el valor por defecto del Mesh.
         */
        public void resetMesh() {
            this.mesh = new WorldMesh(Main.world.getSize() * Main.world.getSize(), 2, 2);
        }

        /**
         * Verifica si se puede construir en este terreno.
         *
         * @return true si se puede construir, false en caso contrario.
         */
        public boolean isBuildable() {
            return this.IS_BUILDABLE;
        }

        /**
         * @return Mesh que está utilizando el tipo de terreno.
         */
        public WorldMesh getMesh() {
            return this.mesh;
        }

        /**
         * @return Textura del tipo de terreno.
         */
        public Texture getTexture() {
            return this.TEXTURE;
        }
    }
}
