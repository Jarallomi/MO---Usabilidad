package mo.core.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtensionPoint {
    
    String id() default "";
    String version() default "0";
    
    String name() default "";
    String description() default "";

}