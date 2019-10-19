package de.tforneberg.patchdb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Collection {
	
	@JsonView(Patch.DefaultView.class)
	private List<Patch> patches;
	
	@JsonView(User.DefaultView.class)
	private String username;

}
