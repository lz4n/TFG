package utils.render.texture;

/**
 * Representa una textura capaz de guardarse en la caché y cargarse en la GPU sin volver a leer la imagen, lo que aumenta
 * el rendimiento del juego y reduce la carga de trabajo de la GPU.
 */
public interface CacheTexture {

    /**
     * Método encargado de cargar la textura y guardarla en la cache. Esto se tiene que hacer cuando <code>LWJGL</code> está cargado.
     */
    void init();

    /**
     * Borra la textura de la caché, para liberar memoria.
     */
    void remove();
}
