package de.tforneberg.patchdb.model.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ChangePasswordRequestData {
	
	@NotNull
	@NotEmpty
    private String password;
	
	@NotNull
	@NotEmpty
    private String password2;
	
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
	public String getPassword2() { return password2; }
	public void setPassword2(String password2) { this.password2 = password2; }

}
