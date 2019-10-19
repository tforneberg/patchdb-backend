package de.tforneberg.patchdb.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequestData {
	
	@NotNull
	@NotEmpty
	private String email;
	
	@NotNull
	@NotEmpty
    private String name;
	
	@NotNull
	@NotEmpty
    private String password;
	
	@NotNull
	@NotEmpty
    private String password2;
	
	@NotNull
	@NotEmpty
	private boolean acceptedTerms;
}
