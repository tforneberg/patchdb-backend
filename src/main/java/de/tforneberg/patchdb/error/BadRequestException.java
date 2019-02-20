package de.tforneberg.patchdb.error;

import org.springframework.validation.BindingResult;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = -2146006695243268255L;
	
	private BindingResult bindingResult;
	
	public BadRequestException(BindingResult bindingResult) {
		super();
		this.bindingResult = bindingResult;
	}
	
	public BindingResult getBindingResult() {
		return bindingResult;
	}


}
