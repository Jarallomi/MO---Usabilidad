package mo.core.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Extends {
    String extensionPointId();
    String extensionPointVersion() default ">=0";
}
