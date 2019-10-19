package de.tforneberg.patchdb.security;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.tforneberg.patchdb.model.User;

@Retention(RUNTIME)
@Target(FIELD)
public @interface HttpPATCHAllowed {
	User.UserStatus[] roles() default User.UserStatus.user;
}
