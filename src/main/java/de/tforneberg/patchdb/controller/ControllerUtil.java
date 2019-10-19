package de.tforneberg.patchdb.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.model.User.UserStatus;
import de.tforneberg.patchdb.security.HttpPATCHAllowed;

public class ControllerUtil {
	
	//TODO
//	static Integer getAuthUserId(Authentication auth) {
//		if (auth.getDetails() instanceof UserDetails) {
//			UserDetails userDetails = (UserDetails) auth.getDetails();
//			userDetails.
//		}
//		return false;
//	}
	
	static <T> boolean updateObjectWithPatchString(String string, T objectToUpdate, Class<T> type, Authentication auth) {
		ObjectMapper objectMapper = new ObjectMapper();
    	try {
    		T objectWithOnlyUpdatedFields = objectMapper.readValue(string, type);
    		
    		Iterator<String> fieldNamesIterator = objectMapper.readTree(string).fieldNames();
			ArrayList<Field> fields = new ArrayList<>();
			
			while (fieldNamesIterator.hasNext()) {
		        fields.add(ReflectionUtils.findField(type, (String) fieldNamesIterator.next()));
			}
			
	        if (!isUserAllowedToDoHttpPATCHRequestOnFields(auth, fields.toArray(new Field[fields.size()]))) return false;
	        
	        for (Field field : fields) {
		        String fieldFirstLetterUppercase = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		        Method setterForField = ReflectionUtils.findMethod(type, "set" + fieldFirstLetterUppercase, field.getType());
		        Method getterForField = ReflectionUtils.findMethod(type, "get" + fieldFirstLetterUppercase);
		        try {
					setterForField.invoke(objectToUpdate, getterForField.invoke(objectWithOnlyUpdatedFields));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					return false;
				}
	        }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	
    	return true;
	}
	
	static boolean isUserAllowedToDoHttpPATCHRequestOnFields(Authentication auth, Field... fields) {
		for (Field field : fields) {
	        HttpPATCHAllowed accessAnnotation = field.getAnnotation(HttpPATCHAllowed.class);
	        if (accessAnnotation == null) return false;
	        
	        boolean userHasPermission = false;
	        for (User.UserStatus role : accessAnnotation.roles()) {
	        	userHasPermission = userHasPermission || hasUserStatus(auth, role);
	        }
	        if (!userHasPermission) return false;
		}
        return true;
	}
	
	static boolean hasUserStatus(Authentication auth, UserStatus userStatus) {
		if (auth != null) {
			Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
			for (GrantedAuthority grantedAuth : authorities) {
				String authString = grantedAuth.getAuthority();
				if (authString.equals(userStatus.toString())) {
					return true;
				}
			}
		}
		return false;
	}
	
	static Pageable getPageable(Optional<Integer> page, Optional<Integer> size, Optional<String> sortBy, Optional<String> direction) {
		Sort sort = null;
		if (sortBy.orElse(null) != null && !sortBy.get().isEmpty()) {
			sort = Sort.by(Direction.fromString(direction.orElse("desc")), sortBy.get());
		}
		return PageRequest.of(page.orElse(0), size.orElse(Integer.MAX_VALUE), sort != null ? sort : Sort.unsorted());
	}
	
	static <T> ResponseEntity<T> getResponseOrNotFound(T result) {
		if (result == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().body(result);
		}
	}
	
	static <T> ResponseEntity<T> getResponseOrBadRequest(T result) {
		if (result == null) {
			return ResponseEntity.badRequest().build();
		} else {
			return ResponseEntity.ok().body(result);
		}
	}
	
	

}
