package world.tick;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Los objetos que se procesen en cada tick que implementen esta anotación se procesarán aunque el juego esté en pausa.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceTicking {
}
