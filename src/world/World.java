package world;

import main.Main;
import utils.Logger;
import world.entity.Entity;
import world.feature.*;
import world.feature.building.Building;
import world.location.Location;
import world.particle.CloudParticle;
import world.particle.Particle;
import world.terrain.Terrain;
import world.tick.Tickable;
import world.tick.Ticking;
import world.worldBuilder.Biome;
import world.worldBuilder.WorldBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Representa el mundo de juego.
 */
public class World extends Ticking implements Serializable {

    /**
     * Duración de las fases del día, meses y años.
     */
    private static final int SUNRISE_DURATION = 6000,
            DAY_DURATION = 48000,
            NIGHT_DURATION = 30000,
            TOTAL_DAY_DURATION = World.SUNRISE_DURATION +World.DAY_DURATION +World.NIGHT_DURATION +World.SUNRISE_DURATION,
            MONTH_DURATION_IN_DAYS = 30,
            MAX_MONTH = 12;
    private static final float MIN_DAYLIGHT = .2f;

    /**
     * Nombre del mundo.
     */
    private final String NAME;

    /**
     * Semilla que se ha usado para la generación del mundo.
     */
    private final int SEED;

    /**
     * Tamaño del mundo en unidades in-game;
     */
    private final int WORLD_SIZE;

    /**
     * WorldBuilder que ha generado el mundo,
     */
    public transient final WorldBuilder BUILDER;

    /**
     * Array que almacena todos los objetos que conforman el terreno.
     */
    private final Terrain[] TERRAIN;

    /**
     * Array que almacena todas las features.
     */
    private final Feature[] FEATURES;

    /**
     * Lista con todas las entidades.
     */
    private final List<Entity> ENTITITES_LIST = new LinkedList<>();

    /**
     * Lista con todas las partículas.
     */
    private transient List<Particle> particlesList = new LinkedList<>();

    /**
     * Hora, dia, mes y año del mundo.
     */
    private int dayTime = (int) (World.SUNRISE_DURATION *.45), day = LocalDateTime.now().getDayOfMonth(), month = LocalDateTime.now().getMonthValue(), year = LocalDateTime.now().getYear();

    /**
     * Número de features y entidades totales del mundo.
     */
    private int featuresCount, entitiesCount;

    /**
     * Índice de felicidad del mundo.
     */
    private float happiness = 1;

    /**
     * Ínstancia un mundo, pero no lo genera.
     * @param name Nombre del mundo.
     * @param seed Semilla del mundo.
     * @param worldSize Tamaño del mundo.
     */
    public World(String name, int seed, int worldSize) {
        this.NAME = name;
        this.SEED = seed;
        this.WORLD_SIZE = worldSize;
        this.TERRAIN = new Terrain[this.WORLD_SIZE * this.WORLD_SIZE];
        this.FEATURES = new Feature[this.WORLD_SIZE * this.WORLD_SIZE];
        this.BUILDER = new WorldBuilder(this.SEED);
    }

    /**
     * Genera el terreno y features en las coordenadas especificadas.
     * @param x Posición en el eje X que se quiere generar.
     * @param y Posición en el eje Y que se quiere generar.
     * @return Terreno que se ha generado.
     */
    private Terrain generateTerrain(int x, int y) {
        double continentality, weirdness, rivers;
        Biome biome;
        Terrain terrain;
        Feature feature = null;

        //Obtenemos los parámetros de generación y el bioma.
        continentality = this.BUILDER.getContinentalityAt(x, y);
        weirdness = this.BUILDER.getWeirdnessAt(x, y);
        rivers = this.BUILDER.getRiversAt(x, y);
        biome = Biome.generateBiome(continentality, weirdness, rivers);

        //Generamos el terreno.
        terrain = new Terrain(biome.getTerrainType(), biome, continentality, weirdness, rivers);
        this.setTerrain(x, y, terrain, false);

        //Generamos las features según el bioma.
        Location location = new Location(x, y);
        switch (biome) {
            case RIVER_MOUNTAIN_SHORE -> {
                if (Main.RANDOM.nextFloat() >= 0.4)
                    feature = Feature.FeatureType.TREE.createFeature(location);
                else if (Main.RANDOM.nextFloat() >= 0.6) feature = Feature.FeatureType.BUSH.createFeature(location);
            }
            case FOREST -> {
                if (Main.RANDOM.nextFloat() >= 0.75)
                    feature = Feature.FeatureType.TREE.createFeature(location);
                else if (Main.RANDOM.nextFloat() >= 0.84) feature = Feature.FeatureType.BUSH.createFeature(location);
            }
            case PLAINS -> {
                if (Main.RANDOM.nextFloat() >= 0.97)
                    feature = Feature.FeatureType.TREE.createFeature(location);
                else if (Main.RANDOM.nextFloat() >= 0.87) feature = Feature.FeatureType.BUSH.createFeature(location);
                else if (Main.RANDOM.nextFloat() >= 0.99) feature = Feature.FeatureType.FLOWER.createFeature(location);
            }
            case MOUNTAIN -> {
                if (Main.RANDOM.nextFloat() >= 0.98)
                    feature = Feature.FeatureType.ROCK.createFeature(location);
            }
        }
        if (feature != null && feature.canBePlaced()) {
            Main.world.addFeature(feature, false);
        }

        return terrain;
    }

    /**
     * Obtenemos el terreno en una determina posición. Si el terreno no se ha generado se genera, y si está fuera del mundo
     * devuelve <code>null</code>.
     * @param x Posición en el eje X de la cual se quiere obtener el terreno.
     * @param y Posición en el eje Y de la cual se quiere obtener el terreno.
     * @return Terreno en dichas coordenadas, o <code>null</code> si está fuera del mundo.
     */
    public Terrain getTerrain(int x, int y) {
        if (new Location(x, y).isOutOfTheWorld()) return null;

        Terrain terrain = this.TERRAIN[this.mapCoordinatesToIndex(x, y)];

        if (terrain == null) {
            terrain = this.generateTerrain(x, y);
        }
        return terrain;
    }

    /**
     * Establece el terreno en una determinada posición.
     * @param x Posición en el eje X dodne se quiere colocar el terreno.
     * @param y Posición en el eje Y donde se quiere colcoar el terreno.
     * @param terrain Terreno que se quiere colocar.
     * @param updateMesh Indica si se van a actualizar los Mesh.
     * @see utils.render.mesh.Mesh
     */
    public void setTerrain(int x, int y, Terrain terrain, boolean updateMesh) {
        int terrainIndex = this.mapCoordinatesToIndex(x, y);
        Terrain oldTerrain = this.TERRAIN[terrainIndex];

        this.TERRAIN[this.mapCoordinatesToIndex(x, y)] = terrain;

        if (oldTerrain != null && !oldTerrain.getType().equals(terrain.getType()) && updateMesh) {
            oldTerrain.getType().updateMesh();
            terrain.getType().updateMesh();
        }

    }

    /**
     * Establece el terreno en una determinada posición.
     * @param x Posición en el eje X dodne se quiere colocar el terreno.
     * @param y Posición en el eje Y donde se quiere colcoar el terreno.
     * @param terrain Terreno que se quiere colocar.
     */
    public void setTerrain(int x, int y, Terrain terrain) {
        this.setTerrain(x, y, terrain, true);

    }

    /**
     * Obtiene la feature en una determinada posición.
     * @param x Posición en el eje X donde se quiere mirar la feature.
     * @param y Posición en el eje Y donde se quiere mirar la feature.
     * @return Feature en dicha posición; <code>null</code> si no hay ninguna feature o si está fuera del mundo.
     */
    public Feature getFeature(int x, int y) {
        try {
            return this.FEATURES[this.mapCoordinatesToIndex(x, y)];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * Genera una feature en el mundo.
     * @param feature Feature que se quiere generar.
     * @param updateMesh Si se va a actualizar el mesh del tipo de feature o no. Cuando se está generando el mundo no se
     * actualiza el mesh.
     * @return Feature generada.
     */
    public Feature addFeature(Feature feature, boolean updateMesh) {
        try {
            //Ocupamos todas las casillas que ocupe la feature.
            int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
            for (int x = 0; x < feature.getSize().x(); x++) {
                for (int y = 0; y < feature.getSize().y(); y++) {
                    this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = feature;
                    if (feature instanceof Building) {
                        Terrain terrain = this.getTerrain(posX +x, posY +y);
                        this.setTerrain(posX + x, posY + y, new Terrain(
                                Terrain.TerrainType.PATH,
                                terrain.getBiome(),
                                terrain.getContinentalityNoise(),
                                terrain.getWeirdnessNoise(),
                                terrain.getRiversNoise()
                        ));
                    }
                }
            }
            Feature.FeatureType featureType = feature.getFeatureType();

            //Actualizamos el mesh.
            if (updateMesh) {
                featureType.updateMesh();
            } else {
                featureType.getMesh().addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getSize().x(), feature.getSize().y(), feature.getVariant());
            }

            if (feature instanceof Tickable tickable) {
                new Ticking(tickable, false);
            }

            this.featuresCount++;
            return feature;
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }
        return null;
    }

    /**
     * Añade una feature y actualiza el mesh automaticamente.
     * @param feature Feature que se quiere generar.
     * @return Feature generada.
     * @see World#addFeature(Feature, boolean)
     */
    public Feature addFeature(Feature feature) {
        return this.addFeature(feature, true);
    }

    /**
     * Elimina una feature del mundo. Eliminar una feature siemrpe actualiza el mesh.
     * @param feature Feature que se quiere eliminar.
     */
    public void removeFeature(Feature feature) {
        try {
            //Limpiamos todas las celdas que ocupe la feature.
            int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
            for (int x = 0; x < feature.getSize().x(); x++) {
                for (int y = 0; y < feature.getSize().y(); y++) {
                    this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = null;
                }
            }

            //Actualizamos el mesh.
            feature.getFeatureType().updateMesh();

            if (feature instanceof Tickable tickable) {
                Ticking.removeTicking(tickable);
            }

            this.featuresCount--;
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }
    }

    /**
     * Genera una entidad.
     * @param entity Entidad que se quiere generar.
     */
    public void spawnEntity(Entity entity) {
        this.ENTITITES_LIST.add(entity);
        this.entitiesCount++;
    }

    /**
     * Genera una partícula.
     * @param particle Partícula que se quiere generar.
     */
    public void spawnParticle(Particle particle) {
        if (this.particlesList != null) {
            this.particlesList.add(particle);
        }
    }

    /**
     * Elimina una partícula y la elimina de ticking.
     * @param particle Partícula que se quiere eliminar.
     * @see Ticking
     */
    public void removeParticle(Particle particle) {
        this.particlesList.remove(particle);
        particle.removeTicking();
    }

    /**
     * @return Nombre del mundo.
     */
    public String getName() {
        return this.NAME;
    }

    /**
     * @return Semilla utilizada para generar el mundo.
     */
    public int getSeed() {
        return this.SEED;
    }

    /**
     * @return Tamaño del mundo.
     */
    public int getSize() {
        return this.WORLD_SIZE;
    }

    /**
     * Convierte coordenadas bidimensionales en unidimensionales para los arrays de terreno y features.
     * @param x Coordenada en el eje X.
     * @param y Coordenada en el eje Y.
     * @return Índice equivalente a la posición que se pasa como parámetros.
     */
    public int mapCoordinatesToIndex(int x, int y) {
        return x * this.WORLD_SIZE + y;
    }

    /**
     * @return Hora del día en ticks.
     * @see World#getFormattedDayTime()
     */
    public int getDayTime() {
        return this.dayTime;
    }

    /**
     * @return Número total de features.
     */
    public int getFeaturesCount() {
        return this.featuresCount;
    }

    /**
     * @return Número total de entidades.
     */
    public int getEntitiesCount() {
        return this.entitiesCount;
    }

    /**
     * Calcula la intensidad de la luz del día. La luz depende de la hora del día.
     * @return Intensidad de la luz del día.
     */
    public float getDayLight() {
        if (this.getDayTime() < World.SUNRISE_DURATION) {
            return World.MIN_DAYLIGHT + (1 - World.MIN_DAYLIGHT) * ((float) (this.getDayTime()) / World.SUNRISE_DURATION); //La intensidad aumenta durante el amanecer
        } else if (this.getDayTime() < World.SUNRISE_DURATION  +World.DAY_DURATION) {
            return 1f; //Máxima intensidad durante el día
        } else if (this.getDayTime() < World.SUNRISE_DURATION +World.DAY_DURATION +World.SUNRISE_DURATION) {
            return 1f - (1 - World.MIN_DAYLIGHT) * ((float) (this.getDayTime() - World.SUNRISE_DURATION -World.DAY_DURATION) / World.SUNRISE_DURATION); //La intensidad disminuye durante el anochecer
        } else {
            return World.MIN_DAYLIGHT; //Sin luz durante la noche
        }
    }

    /**
     * @return Hora del día en formato HH:mm
     */
    public String getFormattedDayTime() {
        return String.format("%02d:%02d",
                this.getDayTime() *24 /World.TOTAL_DAY_DURATION,
                this.getDayTime() *24 %World.TOTAL_DAY_DURATION *60 /World.TOTAL_DAY_DURATION
                );
    }

    /**
     * @return Día del mes del mundo.
     */
    public int getDay() {
        return this.day;
    }

    /**
     * @return Mes del año del mundo
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * @return Año del mundo.
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @return Índice de felicidad.
     */
    public float getHappiness() {
        return this.happiness;
    }

    /**
     * @return Lista con todas las features.
     */
    public List<Feature> getFeatures() {
        return new LinkedList<>(Arrays.asList(this.FEATURES));
    }

    /**
     * @return Lista con todas las entidades.
     */
    public LinkedList<Entity> getEntities() {
        return new LinkedList<>(this.ENTITITES_LIST);
    }

    /**
     * @return Lista con todas las partículas.
     */
    public List<Particle> getParticlesList() {
        if (this.particlesList == null) {
            this.particlesList = new LinkedList<>();
        }
        return new LinkedList<>(this.particlesList);
    }

    @Override
    public void onTick(long deltaTime) {

        this.dayTime++;
        if (this.dayTime >= World.TOTAL_DAY_DURATION) { //Si ha pasado un día
            //TODO Mecánica de felicidad, que sea random cada dia es temporal.
            this.happiness += (Main.RANDOM.nextFloat() /10) * (Main.RANDOM.nextBoolean()?1:-1);
            if (this.happiness > 1) {
                this.happiness = 1;
            }
            if (this.happiness < 0) {
                this.happiness = 0;
            }

            this.dayTime = 0;

            //Aumentamos el mes y año si hace falta..
            this.day++;
            if (this.day >= World.MONTH_DURATION_IN_DAYS) {
                this.month++;
                this.day = 0;

                if (this.month > World.MAX_MONTH) {
                    this.year++;
                    this.month = 0;
                }
            }
        }

        //Generamos nubes.
        if (Main.RANDOM.nextFloat() > 0.98) {
            float cloudVelocity = Main.RANDOM.nextFloat(.04f, .06f);
            Location cloudLocation = new Location(0, Main.RANDOM.nextFloat(this.WORLD_SIZE));
            this.spawnParticle(new CloudParticle(cloudLocation.clone(), cloudVelocity, false));

            //Generamos la sombra.
            cloudLocation.add(0, Main.RANDOM.nextFloat(2f, 5f) *-1);
            this.spawnParticle(new CloudParticle(cloudLocation, cloudVelocity, true));
        }
    }
}
