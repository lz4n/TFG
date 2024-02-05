package ui.widget.widgetUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hace que el widget que implemente esta anotación no tendrá en cuenta el desplazamiento del scroll.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IgnoreScrollMovement {
}
