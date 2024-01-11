package world.particle;

import org.joml.Vector2f;
import world.location.Location;

/**
 * Representa una partícula con gravedad.
 */
public abstract class AbstractPhysicsParticle extends Particle {
    /**
     * Vector que indica la velocidad de la partícula.
     */
    protected Vector2f motion;

    /**
     * Aceleración <srtrong>máxima</srtrong> de la gravedad.
     */
    protected float maxGravity;

    /**
     * Aceleración de la gravedad.
     */
    protected float gravityVelocity;

    /**
     * Cuánto disminuye o aumenta la velocidad por tick.
     */
    protected float motionDecay;

    /**
     * @param location Posición donde se va a generar la partícula.
     * @param motion Vector que indica la velocidad de la partícula.
     * @param motionDecay Cuánto disminuye o aumenta la velocidad por tick.
     * @param maxGravity Aceleración <strong>máxima</strong> de la gravedad.
     * @param gravityVelocity Aceleración de la gravedad.
     */
    public AbstractPhysicsParticle(Location location, Vector2f motion, float motionDecay, float maxGravity, float gravityVelocity) {
        super(location);
        this.motion = motion;
        this.motionDecay = motionDecay;
        this.maxGravity = maxGravity;
        this.gravityVelocity = gravityVelocity;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        //Movemos la partícula según su velocidad.
        super.location.add(this.motion.x(), this.motion.y());

        //Modificamos la velocidad según el motionDecay.
        this.motion.add(this.motionDecay *AbstractPhysicsParticle.getDirection(this.motion.x()), this.motionDecay *AbstractPhysicsParticle.getDirection(this.motion.y()));

        //Calculamos la gravedad
        if (this.motion.y() <= this.maxGravity) { //Si la partícula no ha alcanzado la velocidad máxima.
            this.motion.set(this.motion.x(), this.maxGravity);
        } else { //Si la partícula ha alcanzaado la velocidad máxima.
            this.motion.sub(0, this.gravityVelocity);
        }
    }

    /**
     * Calcula la sentido que tiene el la velocidad.
     * @param num Velocidad de la cúal se quiese sacar la sentido.
     * @return Número entero que indica el sentido de la velocidad. Si la velocidad es 0, devuelve 0. Si es positiva devuelve 1, y si es negativa devuelve -1.
     */
    private static int getDirection(float num) {
        if (num == 0) return 0;
        return num>0?-1:1;
    }
}
