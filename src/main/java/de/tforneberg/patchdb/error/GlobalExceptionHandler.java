package de.tforneberg.patchdb.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<GlobalBadRequestResponse> handleException(BadRequestException e) {
		GlobalBadRequestResponse response;
		if (e != null) {
			response = new GlobalBadRequestResponse(e.getBindingResult());
		} else {
			response = new GlobalBadRequestResponse("", "Bad request");
		}
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<GlobalBadRequestResponse> handleException(ResourceNotFoundException e) {
		GlobalBadRequestResponse response = new GlobalBadRequestResponse("", "Resource not found");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler
	public ResponseEntity<GlobalBadRequestResponse> handleException(Exception e) {
		GlobalBadRequestResponse response = new GlobalBadRequestResponse("", "Bad request");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
