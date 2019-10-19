package de.tforneberg.patchdb.controller;

public class Constants {
	public static final String ID_MAPPING = "/{id}";
	
	public static final String AUTH_ADMIN_OR_MOD = "hasAuthority('admin') || hasAuthority('mod')";
	public static final String AUTH_ADMIN = "hasAuthority('admin')";
	public static final String AUTH_MOD = "hasAuthority('mod')";
	public static final String LOGGED_IN = "isAuthenticated()";
	public static final String AUTH_ID_IS_OF_REQUESTING_USER = "@userUtils.mapIDtoUsername(#id) == authentication.principal.username";
	
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	public static final String SORTBY = "sortBy";
	public static final String DIRECTION = "direction";
}
