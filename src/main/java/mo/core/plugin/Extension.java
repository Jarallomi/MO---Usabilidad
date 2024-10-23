package mo.core.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Celso
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    
    String id() default "";
    String version() default "0";
    
    String name() default "";
    String description() default "";

    public Extends[] xtends();
}

//@Extension(
//        codeName = "",
//        packageName = "",
//        fullName = "",
//        name = "",
//        description = "",
//        interfacesThatImplements = {""},
//        interfacesThatProvide = {""}
//)