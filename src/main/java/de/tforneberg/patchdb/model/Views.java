package de.tforneberg.patchdb.model;

public class Views {
	public static interface BriefView {}
	
    public static interface DefaultView extends BriefView {}
    
    public static interface CompleteView extends DefaultView {}
}
