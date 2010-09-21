package org.springframework.social.facebook;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates that a controller method parameter should be bound
 * to the Facebook access token given when the user signed in with Facebook.
 * 
 * @author Craig Walls
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FacebookAccessToken {

}
