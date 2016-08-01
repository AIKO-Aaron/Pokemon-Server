package ch.aiko.modloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Mod {

	//Class<?> initClass();
	String modName() default "";
	String modAuthor() default "";
	int majorVersion() default 0;
	int minorVersion() default 0;
	int patch() default 0;
	
}
