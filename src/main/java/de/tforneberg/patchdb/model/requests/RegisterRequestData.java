package de.tforneberg.patchdb.model.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    
	public boolean isAcceptedTerms() { return acceptedTerms; }
	public void setAcceptedTerms(boolean acceptedTerms) { this.acceptedTerms = acceptedTerms; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
	public String getPassword2() { return password2; }
	public void setPassword2(String password2) { this.password2 = password2; }

}
