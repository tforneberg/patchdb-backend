package de.tforneberg.patchdb.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequestData {
	
	@NotNull
	@NotEmpty
    private String password;
	
	@NotNull
	@NotEmpty
    private String password2;
}
