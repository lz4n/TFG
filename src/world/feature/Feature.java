package world.feature;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.WorldMesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.feature.building.House;
import world.location.Location;

import java.io.Serializable;
import java.util.*;

/**
 * Decoradores generados automaticamente o estructuras colocadas por el jugador. Las features se asignan a una celda o varias
 * celdas del juego, y el jugador podrá interactuar con ellas cuando haga clic sobre esas casillas.
 *
 * Un mismo tipo de feature
 */
public abstract class Feature implements Comparable<Feature>, Serializable {

    /**
     * Posición donde está hubicada la feature.
     */
    private final Location LOCATION;

    /**
     * Tamaño de la feature en celdas.
     */
    private final Vector2i SIZE_IN_BLOCKS;

    /**
     * Tipo de feature.
     */
    private final FeatureType FEATURE_TYPE;

    /**
     * Variante de la feature.
     */
    private final int VARIANT;

    public Feature(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {

        //Calculamos el desplazamiento, para que las features no estén todas en la misma posición dentro de la casilla.
        this.LOCATION = location.clone();
        this.SIZE_IN_BLOCKS = sizeInBlocks;
        this.FEATURE_TYPE = featureType;
        this.VARIANT = variant;

        float offsetX =  Main.RANDOM.nextFloat() *this.getRandomOffset().x();
        float offsetY =  Main.RANDOM.nextFloat() *this.getRandomOffset().x();
        this.LOCATION.add(offsetX, offsetY);
    }

    /**
     * @return Posición donde está hubicada 
     */
    public Location getLocation() {
        return this.LOCATION.clone();
    }
    /**
     * @return Tipo de feature.
     */
    public FeatureType getFeatureType() {
        return this.FEATURE_TYPE;
    }

    /**
     * @return Vector bidimiensional de enteros que representa el tamaño de la feature.
     */
    public Vector2i getSize() {
        return new Vector2i(this.SIZE_IN_BLOCKS);
    }

    /**
     * @return Variante de la feature que se está mostrando.
     */
    public int getVariant() {
        return this.VARIANT;
    }

    /**
     * Determina si la feature puede ser colocada en el mundo o no. Para eso mira si hay alguna feature ya colocada en el
     * área que va a ocupar la nueva feature. Si no hay ninguna comprueba las condiciones específicas de esa feature.
     * @return Si la feature puede ser colocada en el mundo o no.
     * @see Feature#checkSpecificConditions()
     */
    public boolean canBePlaced() {
        int posX = (int) this.getLocation().getX(), posY = (int) this.getLocation().getY();
        for (int x = 0; x < this.getSize().x(); x++) {
            for (int y = 0; y < this.getSize().y(); y++) {
                if (Main.world.getFeature(posX +x, posY +y) != null) return false;
                if (new Location(posX +x, posY +y).isOutOfTheWorld()) {
                    return false;
                }
            }
        }
        try {
            return this.checkSpecificConditions();
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    /**
     * Comprueba las condiciones específicas de colocación de una feature, por ejemplo, que un árbol sólo se pueda colocar
     * sobre cesped.
     * @return Si las condiciones específicas de colocacion han sido superadas o no.
     */
    protected abstract boolean checkSpecificConditions();

    public Vector2f getRealSize() {
        return new Vector2f(this.getSize().x(), this.getSize().y());
    }

    /**
     * @return Desplazamiento máximo que puede tener la feature.
     */
    public Vector2f getRandomOffset() {
        return new Vector2f((this.getSize().x() - this.getRealSize().x()));
    }

    @Override
    public int hashCode() {
        return this.LOCATION.hashCode();
    }

    @Override
    public int compareTo(Feature feature) {
        int compareY = Double.compare(feature.getLocation().getY(), this.getLocation().getY());
        if (compareY == 0) {
            return Double.compare(feature.getLocation().getY(), this.getLocation().getY());
        }
        return compareY;
    }

    /**
     * Tipo de feature. El tipo de feature determina las posibles variantes y contiene el <code>Mesh</code> encargado de
     * renderizar las features de ese tipo.
     * @see WorldMesh
     */
    public enum FeatureType implements Serializable {
        ROCK(
                Textures.ROCK
        ),
        FLOWER(
                Textures.TULIP,
                Textures.TULIP_2,
                Textures.BLUE_ORCHID,
                Textures.DANDELION,
                Textures.RED_LILY
        ),
        BUSH(
                Textures.BUSH
        ),
        TREE(
                Textures.TREE1,
                Textures.TREE2
        ),
        HOUSE(
                Textures.HOUSE_LVL1,
                Textures.HOUSE_LVL2,
                Textures.HOUSE_LVL3,
                Textures.HOUSE_LVL4,
                Textures.HOUSE_LVL5,
                Textures.HOUSE_LVL6,
                Textures.HOUSE_LVL7

        );

        /**
         * Mesh que va a renderizar las features de un mismo tipo.
         */
        private WorldMesh mesh;

        /**
         * Lista de variantes que puede tener una feature.
         */
        private final List<Texture> TEXTURES;

        /**
         * Número de variantes que tiene una feature.
         */
        private final int VARIANTS;

        /**
         * @param textures Variantes de features que puede tener ese tipo.
         */
        FeatureType(Texture... textures) {
            this.mesh = new WorldMesh(Main.world.getSize() * Main.world.getSize(), 2, 2, 1);
            this.TEXTURES = Arrays.asList(textures);
            this.VARIANTS = textures.length;
        }

        /**
         * Actualiza el mesh, recalcula el mesh para todas las features de ese tipo.
         */
        public void updateMesh() {
            List<Feature> features = Main.world.getFeatures();
            features.removeIf(feature -> feature == null || !feature.getFeatureType().equals(this));
            this.mesh = new WorldMesh(features.size(), 2, 2, 1);
            features.forEach(feature -> mesh.addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getRealSize().x(), feature.getRealSize().y(), feature.VARIANT));
            this.mesh.load();
        }

        /**
         * @return <code>Mehs</code> encargado de renderizar las features.
         */
        public WorldMesh getMesh() {
            return this.mesh;
        }

        /**
         * @return Texturas de las posibles variantes de una feature.
         */
        public List<Texture> getTextures() {
            return new LinkedList<>(this.TEXTURES);
        }

        /**
         * @return Número de variantes que tiene la feature.
         */
        public int getVariants() {
            return this.VARIANTS;
        }

        /**
         * Crea una feature con una variante determinada. Crea el objeto, pero no lo coloca en el mundo.
         * @param location Posición donde se va a generar la feature.
         * @param variant Variante de la feature que se va a generar.
         * @return Nueva feature.
         */
        public Feature createFeature(Location location, int variant) {
            switch (this) {
                case ROCK -> {
                    return new Rock(location);
                }
                case BUSH -> {
                    return new Bush(location);
                }
                case TREE -> {
                    return new Tree(location, variant);
                }
                case FLOWER -> {
                    return new Flower(location, variant);
                }
                case HOUSE -> {
                    return new House(location, variant);
                }
            }
            return null;
        }

        /**
         * Crea una feature con una variante aleatoria. Crea el objeto, pero no lo coloca en el mundo.
         * @param location Posición donde se va a generar la feature.
         * @return Nueva feature.
         */
        public Feature createFeature(Location location) {
            return createFeature(location, Main.RANDOM.nextInt(this.VARIANTS));
        }
    }
}
