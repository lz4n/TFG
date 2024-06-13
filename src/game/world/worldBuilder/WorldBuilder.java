package game.world.worldBuilder;

import org.abrun.fastNoise.NoiseGenerator;

/**
 * Se encarga de generar los mapas de ruido del mundo.
 */
public class WorldBuilder {
    private final int OCTAVES = 8;
    private final float ROUGHNESS = 0.43f, SCALE = 0.8f;

    private final NoiseGenerator CONTINENTALITY_NOISE, WEIRDNESS_NOISE, RIVERS_NOSISE;

    /**
     * Crea una instancia de WorldBuilder con la semilla especificada.
     *
     * @param seed Semilla con la que se genera el mundo, dos mundos con la misma semilla tienen el mismo terreno.
     */
    public WorldBuilder(int seed) {
        this.CONTINENTALITY_NOISE = new NoiseGenerator(seed) {{SetNoiseType(NoiseType.OpenSimplex2);}};
        this.WEIRDNESS_NOISE = new NoiseGenerator(seed+1) {{SetNoiseType(NoiseType.OpenSimplex2);}};
        this.RIVERS_NOSISE = new NoiseGenerator(seed+2) {{
            SetNoiseType(NoiseType.OpenSimplex2);
        }};
    }

    /**
     * Obtiene el valor de ruido de continentalidad en las coordenadas especificadas.
     *
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return Valor de ruido de continentalidad en las coordenadas especificadas.
     */
    public double getContinentalityAt(int x, int y) {
        return this.getNoiseAt(x, y, this.CONTINENTALITY_NOISE, this.SCALE, this.OCTAVES, this.ROUGHNESS);
    }


    /**
     * Obtiene el valor de ruido de rareza en las coordenadas especificadas.
     *
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return Valor de ruido de rareza en las coordenadas especificadas.
     */
    public double getWeirdnessAt(int x, int y) {
        return this.getNoiseAt(x, y, this.WEIRDNESS_NOISE, this.SCALE, this.OCTAVES, this.ROUGHNESS);
    }

    /**
     * Obtiene el valor de ruido de los ríos en las coordenadas especificadas.
     *
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return Valor de ruido de los ríos en las coordenadas especificadas.
     */
    public double getRiversAt(int x, int y) {
        return this.getNoiseAt(x, y, this.RIVERS_NOSISE, this.SCALE /3, this.OCTAVES, this.ROUGHNESS * 1.5);
    }

    /**
     * Calcula el valor de ruido en las coordenadas especificadas utilizando el generador de ruido especificado.
     *
     * @param x             Coordenada X.
     * @param y             Coordenada Y.
     * @param noiseGenerator Generador de ruido a utilizar.
     * @param scale         Escala del ruido.
     * @param octaves       Número de octavas.
     * @param roughness     Aspereza del ruido.
     * @return Valor de ruido calculado en las coordenadas especificadas.
     */
    public double getNoiseAt(int x, int y, NoiseGenerator noiseGenerator, double scale, double octaves, double roughness) {
        double noise = 0;
        double layerFrequency = this.SCALE;
        double layerWeight = 1;

        for (int octave = 0; octave < this.OCTAVES; octave++) {
            noise += noiseGenerator.GetNoise((float) (x * layerFrequency), (float) (y * layerFrequency)) * layerWeight;
            layerFrequency *= 2;
            layerWeight *= this.ROUGHNESS;
        }
        return noise;
    }
}
