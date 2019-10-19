package de.tforneberg.patchdb.model;

import javax.persistence.Transient;

import lombok.Data;

@Data
public class Patchable {
	
	public static enum HttpPatchOperation {add, remove, replace};

	@Transient
	protected HttpPatchOperation operation;
	
	@Transient
	protected String path;
	
	@Transient
	protected String value;
}
