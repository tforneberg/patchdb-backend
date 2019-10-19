package de.tforneberg.patchdb.model;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name="bands")
@Data @AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(Band.DefaultView.class)
public class Band {
	
	@JsonIgnoreProperties public static interface BriefView {}
	@JsonIgnoreProperties public static interface DefaultView extends BriefView {}
	@JsonIgnoreProperties public static interface CompleteView extends DefaultView {}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="patches", joinColumns=@JoinColumn(name="band_id"))
	@Column(name="id")
	@JsonView(CompleteView.class)
	private Set<Integer> patchIDs;
	
	public Band(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
