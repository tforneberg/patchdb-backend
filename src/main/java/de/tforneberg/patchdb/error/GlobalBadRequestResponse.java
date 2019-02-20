package de.tforneberg.patchdb.error;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class GlobalBadRequestResponse {
	
	private String origin;
	private String code;
	
	public GlobalBadRequestResponse(String origin, String code) {
		this.origin = origin;
		this.code = code;
	}
	
	public GlobalBadRequestResponse(BindingResult result) {
		FieldError fieldErr = result.getFieldError();
		ObjectError globErr = result.getGlobalError();
		if (fieldErr != null) {
			this.origin = fieldErr.getField();
			this.code = fieldErr.getCode();
		} else if (globErr != null) {
			this.origin = globErr.getObjectName();
			this.origin = globErr.getCode();
		}
	}
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	

}
