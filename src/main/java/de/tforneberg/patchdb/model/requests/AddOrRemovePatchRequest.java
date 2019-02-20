package de.tforneberg.patchdb.model.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AddOrRemovePatchRequest {
	
	@NotNull
	@NotEmpty
	private String op;
	
	@NotNull
	@NotEmpty
	private int value;
	
	@NotNull
	@NotEmpty
	private String path;
	
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
