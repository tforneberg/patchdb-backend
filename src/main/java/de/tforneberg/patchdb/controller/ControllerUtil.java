package de.tforneberg.patchdb.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tforneberg.patchdb.model.User.UserStatus;
import de.tforneberg.patchdb.security.HttpPATCHAllowed;

@Log4j2
public class ControllerUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Updates a given object (objectToUpdate) of given type (type) with the given string (string). 
	 * Therefore, the string (containing a JSON stringified part of the object to update, containing only the fields that should be updated)
	 * gets converted into a object of the given type. After this, the fields of the given object get set with the fields from the 
	 * parsed object. This method also checks the Authentication of the given user to update the fields, first.
	 * The fields need a HttpPatchAllowed annotation containing the role of auth
	 * 
	 * @param string the string containing a JSON Patch (a part of the object to update, containing only the fields with new values) 
	 * @param objectToUpdate the object to update
	 * @param type the type of the object to update
	 * @return true if the operation is successful, false otherwise
	 */
	static <T> boolean updateObjectWithPatchString(String string, T objectToUpdate, Class<T> type) {
    	try {
    		T objectWithOnlyUpdatedFields = objectMapper.readValue(string, type);

	        for (Field field : getFieldsFromJSONRequest(string, type)) {
		        String fieldFirstLetterUppercase = StringUtils.capitalize(field.getName());
		        Method setterForField = ReflectionUtils.findMethod(type, "set" + fieldFirstLetterUppercase, field.getType());
		        Method getterForField = ReflectionUtils.findMethod(type, "get" + fieldFirstLetterUppercase);
				if (setterForField != null && getterForField != null) {
					setterForField.invoke(objectToUpdate, getterForField.invoke(objectWithOnlyUpdatedFields));
				}
			}
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
			return false;
		}
    	
    	return true;
	}

	public static <T> boolean isUserAllowedToDoPATCHRequest(String request, Class<T> type, Authentication auth) {
		try {
			return getFieldsFromJSONRequest(request, type).stream()
					.allMatch(field -> isUserAllowedToDoPATCHOnField(auth, field));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	private static <T> List<Field> getFieldsFromJSONRequest(String string, Class<T> type) throws IOException {
		return Streams.stream(objectMapper.readTree(string).fieldNames())
				.map(fieldName -> ReflectionUtils.findField(type, fieldName))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private static boolean isUserAllowedToDoPATCHOnField(Authentication auth, Field field) {
		HttpPATCHAllowed annotation = field.getAnnotation(HttpPATCHAllowed.class);
		return annotation != null && Arrays.stream(annotation.roles())
				.anyMatch(role -> hasUserStatus(auth, role));
	}
	
	/**
	 * Checks if the given user/authentication has the given UserStatus in his authority string
	 */
	static boolean hasUserStatus(Authentication auth, UserStatus userStatus) {
		return auth != null && auth.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(authString -> authString.equals(userStatus.toString()));
	}

	/**
	 * Checks if the given user/authentication has any of the given UserStatus in his authority string
	 */
	static boolean hasUserAnyStatus(Authentication auth, UserStatus... userStatusArray) {
		return auth != null && auth.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(authString ->
						Arrays.stream(userStatusArray)
								.map(Enum::toString)
								.collect(Collectors.toList())
								.contains(authString));
	}
	
	static Pageable getPageable(Optional<Integer> page, Optional<Integer> size, Optional<String> sortBy, Optional<String> direction) {
		Sort sort = null;
		if (sortBy.orElse(null) != null && !sortBy.get().isEmpty()) {
			sort = Sort.by(Direction.fromString(direction.orElse("desc")), sortBy.get());
		}
		return PageRequest.of(page.orElse(0), size.orElse(Integer.MAX_VALUE), sort != null ? sort : Sort.unsorted());
	}
	
	static <T> ResponseEntity<T> getResponseOrNotFound(T result) {
		return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(result);
	}
	
	static <T> ResponseEntity<T> getResponseOrBadRequest(T result) {
		return result == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok().body(result);
	}

}
