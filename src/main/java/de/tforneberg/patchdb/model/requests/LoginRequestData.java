package de.tforneberg.patchdb.model.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginRequestData {
	
	@NotNull
	@NotEmpty
    private String name;
	
	@NotNull
	@NotEmpty
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
