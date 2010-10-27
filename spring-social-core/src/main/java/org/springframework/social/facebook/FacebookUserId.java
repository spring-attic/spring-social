package org.springframework.social.facebook;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates to {@link FacebookWebArgumentResolver} that a
 * controller method parameter should be bound to the user's Facebook ID,
 * assuming that user has signed in with Facebook.
 * 
 * @author Craig Walls
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FacebookUserId {

}
