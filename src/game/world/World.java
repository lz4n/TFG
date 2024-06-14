package game.world;

import game.Main;
import game.utils.Logger;
import game.utils.Setting;
import game.utils.render.mesh.Mesh;
import game.world.feature.*;
import game.world.feature.building.Hospital;
import game.world.feature.building.PoliceStation;
import game.world.entity.Duck;
import game.world.feature.*;
import game.world.feature.building.Building;
import game.world.feature.building.jobSite.Port;
import game.world.location.Location;
import game.world.particle.CloudParticle;
import game.world.particle.Particle;
import game.world.terrain.Terrain;
import game.world.worldBuilder.Biome;
import game.world.worldBuilder.WorldBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Representa el mundo del juego.
 * Esta clase contiene toda la lógica y los elementos necesarios para la simulación del mundo.
 * Implementa {@code Serializable} para permitir la serialización del estado del mundo.
 */
public class World implements Serializable {

    /**
     * Duración de las fases del día, meses y años en ticks.
     */
    public static final int
            TOTAL_DAY_DURATION = 4000,
            SUNRISE_START = World.hourToTick(7, 0),
            SUNRISE_END = World.hourToTick(8, 30),
            SUNRISE_DURATION = SUNRISE_END - SUNRISE_START,
            SUNSET_START = World.hourToTick(21, 30),
            SUNSET_END = World.hourToTick(22, 0),
            sunsetDuration = SUNSET_END - SUNSET_START,
            MONTH_DURATION_IN_DAYS = 30,
            MAX_MONTH = 12;
    private static final float MIN_DAYLIGHT = .2f;

    private static final int INITIAL_MONEY = 600;
    
    private final int SEED;

    private final int WORLD_SIZE;

    /**
     * WorldBuilder utilizado para generar el mundo.
     */
    public transient final WorldBuilder BUILDER;

    private final Terrain[] TERRAIN;

    private final Feature[] FEATURES;

    private List<Building> BUILDINGS = new ArrayList<>();

    private final List<Duck> DUCKS_LIST = new LinkedList<>();

    private transient List<Particle> particlesList = new LinkedList<>();

    private int dayTime = World.SUNRISE_START, day = LocalDateTime.now().getDayOfMonth(), month = LocalDateTime.now().getMonthValue(), year = LocalDateTime.now().getYear();

    private int featuresCount, ducksCount;

    private float happiness = 1, unemployment = 1, security = 1, health = 1;

    private int money = World.INITIAL_MONEY, incomes = 0, population = 0;

    /**
     * Instancia un mundo, pero no lo genera.
     * @param seed Semilla del mundo.
     * @param worldSize Tamaño del mundo.
     */
    public World(int seed, int worldSize) {
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
        generateFeaturesAt(x, y, biome, feature);

        return terrain;
    }

    private static void generateFeaturesAt(int x, int y, Biome biome, Feature feature) {
        Location location = new Location(x, y);
        switch (biome) {
            case RIVER_MOUNTAIN_SHORE -> {
                if (Main.RANDOM.nextFloat() >= 0.4)
                    feature = new Tree(location, Main.RANDOM.nextInt(Feature.FeatureType.TREE.getVariants()));
                else if (Main.RANDOM.nextFloat() >= 0.6) feature = new Bush(location);
            }
            case FOREST -> {
                if (Main.RANDOM.nextFloat() >= 0.75)
                    feature = new Tree(location, Main.RANDOM.nextInt(Feature.FeatureType.TREE.getVariants()));
                else if (Main.RANDOM.nextFloat() >= 0.9) feature = new Bush(location);
            }
            case PLAINS -> {
                if (Main.RANDOM.nextFloat() >= 0.97)
                    feature = new Tree(location, Main.RANDOM.nextInt(Feature.FeatureType.TREE.getVariants()));
                else if (Main.RANDOM.nextFloat() >= 0.97) feature = new Bush(location);
                else if (Main.RANDOM.nextFloat() >= 0.99) feature = new Flower(location, Main.RANDOM.nextInt(Feature.FeatureType.FLOWER.getVariants()));
            }
            case MOUNTAIN -> {
                if (Main.RANDOM.nextFloat() >= 0.98)
                    feature = new Rock(location);
            }
        }
        if (feature != null && feature.canBePlaced()) {
            Main.world.addFeature(feature, false);
        }
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
     * @see Mesh
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
            Logger.sendMessage("Error obteniendo una Feature en x=%d,y=%d: %s", Logger.LogMessageType.WARNING, x, y, exception.getMessage());
            return null;
        }
    }

    /**
     * Añade una feature en el mundo.
     * @param feature Feature que se quiere generar.
     * @param updateMesh Si se va a actualizar el mesh del tipo de feature o no. Cuando se está generando el mundo no se
     * actualiza el mesh.
     * @return Feature generada.
     */
    public Feature addFeature(Feature feature, boolean updateMesh) {
        try {
            //Ocupamos todas las casillas que ocupe la feature.
            int posX = (int) feature.getLocation().getX(), posY = (int) feature.getLocation().getY();
            for (int x = 0; x < feature.getBlockSize().x(); x++) {
                for (int y = 0; y < feature.getBlockSize().y(); y++) {
                    this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = feature;
                    if ((feature instanceof Building) && !(feature instanceof Port)) {
                        Terrain terrain = this.getTerrain(posX +x, posY +y);
                        this.setTerrain(posX + x, posY + y, new Terrain(
                                (terrain.getType().equals(Terrain.TerrainType.STONE) || terrain.getType().equals(Terrain.TerrainType.SNOW) || terrain.getType().equals(Terrain.TerrainType.STONE_TILE))? Terrain.TerrainType.STONE_TILE : Terrain.TerrainType.PATH,
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
                featureType.getMesh().addVertex(feature.getLocation().getX(), feature.getLocation().getY(), feature.getBlockSize().x(), feature.getBlockSize().y(), feature.getVariant());
            }

            if (feature instanceof Building building) {
                this.BUILDINGS.add(building);
                building.onAdd();

                this.updateIncomes();
            }

            this.featuresCount++;
            return feature;
        } catch (ArrayIndexOutOfBoundsException exception) {
            Logger.sendMessage("Error intentando colocar una Feature: %s", Logger.LogMessageType.WARNING, exception.getMessage());
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
            for (int x = 0; x < feature.getBlockSize().x(); x++) {
                for (int y = 0; y < feature.getBlockSize().y(); y++) {
                    this.FEATURES[this.mapCoordinatesToIndex(posX + x, posY + y)] = null;
                }
            }

            //Actualizamos el mesh.
            feature.getFeatureType().updateMesh();

            if (feature instanceof Building building) {
                this.BUILDINGS.remove(building);
                building.onRemove();

                this.updateIncomes();
            }

            this.featuresCount--;
        } catch (ArrayIndexOutOfBoundsException exception) {
            Logger.sendMessage("Error intentando eliminar una Feature: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }
    }

    /**
     * Genera una un pato.
     * @param duck Pato que se quiere generar.
     */
    public void addDuck(Duck duck) {
        this.DUCKS_LIST.add(duck);
        this.ducksCount++;

        this.updateUnemployment();
        this.updateSecurity();
        this.updateHealth();
    }

    /**
     * Elimina una pato.
     * @param duck Pato que se quiere eliminar.
     */
    public void removeDuck(Duck duck) {
        this.DUCKS_LIST.remove(duck);
        duck.removeJobSite();
        this.ducksCount--;

        this.updateSecurity();
        this.updateHealth();
    }

    /**
     * Genera una partícula.
     * @param particle Partícula que se quiere generar.
     */
    public void addParticle(Particle particle) {
        if (this.particlesList != null) {
            this.particlesList.add(particle);
        }
    }

    /**
     * Elimina una partícula.
     * @param particle Partícula que se quiere eliminar.
     */
    public void removeParticle(Particle particle) {
        this.particlesList.remove(particle);
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
    public int getDucksCount() {
        return this.ducksCount;
    }

    /**
     * Calcula la intensidad de la luz del día. La luz depende de la hora del día.
     * @return Intensidad de la luz del día.
     */
    public float getDayLight() {

        if ((0 <= this.getDayTime() && this.getDayTime() < SUNRISE_START) || (SUNSET_END <= this.getDayTime() && this.getDayTime() < World.TOTAL_DAY_DURATION)) {
            return World.MIN_DAYLIGHT;
        } else if (SUNRISE_START <= this.getDayTime() && this.getDayTime() < SUNRISE_END) {
            // Durante el amanecer, incrementa la luz gradualmente
            return World.MIN_DAYLIGHT + (1 - World.MIN_DAYLIGHT) * ((float) (this.getDayTime() - SUNRISE_START) / SUNRISE_DURATION);
        } else if (SUNRISE_END <= this.getDayTime() && this.getDayTime() < SUNSET_START) {
            // Durante el día, luz máxima
            return 1;
        } else if (SUNSET_START <= this.getDayTime() && this.getDayTime() <= SUNSET_END) {
            // Durante el atardecer, disminuye la luz gradualmente
            return 1f - (1 - World.MIN_DAYLIGHT) * ((float) (this.getDayTime() - SUNSET_START) / sunsetDuration);
        } else {
            return -1;
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
     * @return Lista con todas las features.
     */
    public List<Feature> getFeatures() {
        return new LinkedList<>(Arrays.asList(this.FEATURES));
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(this.BUILDINGS);
    }

    /**
     * @return Lista con todas las entidades.
     */
    public LinkedList<Duck> getDucks() {
        return new LinkedList<>(this.DUCKS_LIST);
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

    /**
     * Añade una cantidad específica de dinero al jugador.
     * Actualiza el total de dinero en el jugador después de añadir la cantidad.
     *
     * @param amount La cantidad de dinero que se quiere añadir.
     */
    public void addMoney(int amount) {
        this.money += amount;

        Main.PLAYER.updateMoney(this.money);
    }

    /**
     * Obtiene la cantidad de dinero actual del jugador en el mundo.
     *
     * @return La cantidad de dinero actual.
     */
    public int getMoney() {
        return this.money;
    }

    /**
     * Obtiene los ingresos actuales del mundo.
     *
     * @return Los ingresos actuales.
     */
    public int getIncomes() {
        return this.incomes;
    }

    /**
     * Obtiene la población actual del mundo.
     *
     * @return La población actual.
     */
    public int getPopulation() {
        return this.population;
    }

    /**
     * Añade una cantidad especificada a la población actual.
     *
     * @param amount La cantidad de población a añadir.
     */
    public void addPopulation(int amount) {
        this.population += amount;

        Main.PLAYER.updatePopulation(this.population);
    }

    /**
     * @return Índice de felicidad.
     */
    public float getHappiness() {
        return this.happiness;
    }

    /**
     * @return Índice de desempleo.
     */
    public float getUnemployment() {
        return this.unemployment;
    }

    /**
     * @return Índice de seguridad.
     */
    public float getSecurity() {
        return this.security;
    }

    /**
     * @return Índice de salud.
     */
    public float getHealth() {
        return this.health;
    }

    /**
     * Calcula los ingresos del mundo según la cantidad y felicidad de los paos y la industria.
     * @see Duck#getTaxes()
     * @see Building#getIncomes()
     */
    public void updateIncomes() {
        this.incomes = (int) this.getDucks().stream().mapToDouble(Duck::getTaxes).sum();

        this.incomes += this.getBuildings().stream().mapToInt(Building::getIncomes).sum();

        Main.PLAYER.updateIncomes(this.getIncomes());
    }

    /**
     * Calcula la felicidad media de los patos.
     */
    public void updateHappiness() {
        OptionalDouble averageHappiness = this.getDucks().stream().mapToDouble(Duck::getHappiness).average();

        if (averageHappiness.isPresent()) {
            this.happiness = (float) averageHappiness.getAsDouble();
        } else {
            this.happiness = 1;
        }

        this.updateIncomes();

        Main.PLAYER.updateHappiness(this.happiness);
    }

    /**
     * Calcula la tasa de desempleo.
     */
    public void updateUnemployment() {
        this.unemployment = this.DUCKS_LIST.stream().filter(Duck::isUnemployed).count() / (float) this.ducksCount;

        this.updateIncomes();

        Main.PLAYER.updateUnemployment(this.unemployment);
    }

    /**
     * Calcula el índice de seguridad.
     */
    public void updateSecurity() {
        int protection = 0;

        for (Building building : this.BUILDINGS) {
            if (building instanceof PoliceStation policeStation) {
                protection += policeStation.getProtection();
            }
        }

        this.security = Math.clamp(protection / (float) this.getDucksCount(), 0 , 1);

        Main.PLAYER.updateSecurity(this.security);
    }

    /**
     * Calcula el índice de salud.
     */
    public void updateHealth() {
        int protection = 0;

        for (Building building : this.BUILDINGS) {
            if (building instanceof Hospital hospital) {
                protection += hospital.getProtection();
            }
        }

        this.health = Math.clamp(protection / (float) this.getDucksCount(), 0, 1);

        Main.PLAYER.updateHealth(this.health);
    }

    /**
     * Método que se llama en cada tick de juego. Simula el paso del tiempo y llama a las funciones de tick del resto de objetos.
     * @param paused Si el juegeo se encuentra en pausa o no.
     */
    public void onTick(boolean paused) {
        if (this.particlesList != null) {
            for (Particle particle : new LinkedList<>(this.particlesList)) {
                particle.onTick();
            }
        }

        if (!paused || Main.tickSpeed != 0) {
            for (Building BUILDING : this.BUILDINGS) {
                BUILDING.onTick();
            }

            List<Duck> duckList = new ArrayList<>(this.DUCKS_LIST);
            Collections.shuffle(duckList);
            int ducks = 0;
            for (Duck duck : duckList) {
                duck.onTick();
                ducks++;

                if (ducks >= 600) {
                    break;
                }
            }

            this.dayTime += Main.tickSpeed;
            if (this.dayTime >= World.TOTAL_DAY_DURATION) { //Si ha pasado un día
                this.dayTime = 0;

                //Aumentamos el mes y año si hace falta..
                this.day++;

                this.addMoney(this.getIncomes());

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
            if (Main.RANDOM.nextFloat() > Setting.CLOUDS.getValue()) {
                float cloudVelocity = Main.RANDOM.nextFloat(.04f, .06f);
                Location cloudLocation = new Location(0, Main.RANDOM.nextFloat(this.WORLD_SIZE));
                this.addParticle(new CloudParticle(cloudLocation.clone(), cloudVelocity, false));

                //Generamos la sombra.
                cloudLocation.add(0, Main.RANDOM.nextFloat(2f, 5f) * -1);
                this.addParticle(new CloudParticle(cloudLocation, cloudVelocity, true));
            }
        }
    }

    /**
     * Convierte horas a ticks.
     * @param hour Hora que se quiere convertir.
     * @param minute Minuto que se quiere convertir.
     * @return Número de ticks correspondientes.
     */
    public static int hourToTick(int hour, int minute) {
        return (int) (
                (hour * World.TOTAL_DAY_DURATION / 24) +
                        ((float) minute * World.TOTAL_DAY_DURATION) / (24 % World.TOTAL_DAY_DURATION * 60)
        );
    }
}
